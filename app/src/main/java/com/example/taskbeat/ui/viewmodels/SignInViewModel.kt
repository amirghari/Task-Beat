package com.example.taskbeat.ui.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.taskbeat.R
import com.example.taskbeat.data.DataRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SignInViewModel(
    private val dataRepo: DataRepository,
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = _currentUser

    fun signInWithGoogle(account: GoogleSignInAccount, context: Context, onResult: (Boolean) -> Unit) {
        _isLoading.value = true
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    Log.d("SignInViewModel", "signInWithGoogle:success")
                    _currentUser.value = auth.currentUser
                    onResult(true)
                } else {
                    Log.w("SignInViewModel", "signInWithGoogle:failure", task.exception)
                    Toast.makeText(context, "Google Sign-In failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    onResult(false)
                }
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
                        onResult(true)
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

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }

    fun getGoogleSignInOptions(context: Context): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
}