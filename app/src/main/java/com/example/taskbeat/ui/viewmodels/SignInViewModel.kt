import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskbeat.R
import com.example.taskbeat.data.DataRepository
import com.example.taskbeat.model.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInViewModel(
    private val dataRepo: DataRepository,
) : ViewModel() {
    private var _isDarkTheme = MutableStateFlow<Boolean>(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val _userAge = MutableStateFlow<Int?>(null)
    val userAge: StateFlow<Int?> = _userAge

    private val _userGender = MutableStateFlow<String?>(null)
    val userGender: StateFlow<String?> = _userGender


    private val _userDisplayName = MutableStateFlow<String?>(null)
    val userDisplayName: StateFlow<String?> = _userDisplayName

    init {
        viewModelScope.launch {
            dataRepo.isDarkThemeFlow.collect { _isDarkTheme.emit(it) }
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount, context: Context, onResult: (Boolean) -> Unit) {
        _isLoading.value = true
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    Log.d("SignInViewModel", "signInWithGoogle:success")
                    _currentUser.value = auth.currentUser
                    // Check if user info is already in the local database
                    val email = auth.currentUser?.email
                    if (email != null) {
                        loadUserFromLocalDatabase(email) {
                            // If the user info is not in the local database, show the additional info dialog
                            onResult(it == null) // If user info is null, request additional info
                        }
                    } else {
                        onResult(true) // Request additional info if email is missing
                    }
                } else {
                    Log.w("SignInViewModel", "signInWithGoogle:failure", task.exception)
                    Toast.makeText(context, "Google Sign-In failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    onResult(false)
                }
            }
    }

    fun saveAdditionalUserInfo(email: String, password: String, age: Int, gender: String, displayName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = User(
                email = email,
                password = password,
                age = age,
                gender = gender,
                displayName = displayName
            )
            dataRepo.addUser(user)
            _userAge.emit(user.age)
            _userGender.emit(user.gender)
        }
    }

    fun signInWithEmail(email: String, password: String, context: Context, onResult: (Boolean) -> Unit) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            _isLoading.value = true
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        Log.d("SignInViewModel", "signInWithEmail:success")
                        _currentUser.value = auth.currentUser
                        loadUserFromLocalDatabase(email) {
                            onResult(it == null)
                        }
                    } else {
                        Log.w("SignInViewModel", "signInWithEmail:failure", task.exception)
                        Toast.makeText(context, "Sign In Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        onResult(false)
                    }
                }
        } else {
            Toast.makeText(context, "Email and password must not be empty", Toast.LENGTH_SHORT).show()
            onResult(false)
        }
    }

    fun loadUserFromLocalDatabase(email: String, onResult: (User?) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = dataRepo.getUserByEmail(email).firstOrNull()
            if (user != null) {
                _userAge.emit(user.age)
                _userGender.emit(user.gender)
                _userDisplayName.emit(user.displayName)
            }
            withContext(Dispatchers.Main) {
                onResult(user)
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
        _userAge.value = null
        _userGender.value = null
    }

    fun getGoogleSignInOptions(context: Context): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    fun registerWithEmail(email: String, password: String, context: Context, onResult: (Boolean) -> Unit) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            _isLoading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        Log.d("SignInViewModel", "registerWithEmail:success")
                        _currentUser.value = auth.currentUser
                        onResult(true)
                    } else {
                        Log.w("SignInViewModel", "registerWithEmail:failure", task.exception)
                        Toast.makeText(context, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        onResult(false)
                    }
                }
        } else {
            Toast.makeText(context, "Email and password must not be empty", Toast.LENGTH_SHORT).show()
            onResult(false)
        }
    }

    fun toggleTheme() = viewModelScope.launch { dataRepo.toggleTheme() }

}