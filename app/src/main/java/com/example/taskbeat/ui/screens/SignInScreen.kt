import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taskbeat.ui.screens.EnumScreens
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider
import com.example.taskbeat.ui.viewmodels.SignInViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.taskbeat.R

@Composable
fun SignInScreen(
    navCtrl: NavController,
    signInVM: SignInViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Launcher to start Google sign-in process
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let {
                firebaseAuthWithGoogle(it, auth) { success ->
                    isLoading = false
                    if (success) {
                        navigateToHome(navCtrl)
                    } else {
                        Toast.makeText(context, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: ApiException) {
            Log.w("SignInScreen", "Google sign in failed", e)
            Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Sign In",
                canNavigateBack = true,
                onNavigateUp = { navCtrl.navigateUp() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (currentUser == null) {
                        // Email TextField
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Password TextField
                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Sign In Button for Email/Password
                        Button(onClick = {
                            isLoading = true
                            emailSignIn(email, password, auth, navCtrl) {
                                isLoading = false
                            }
                        }) {
                            Text("Sign In with Email")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Register Button for New Users
                        Button(onClick = {
                            isLoading = true
                            emailRegister(email, password, auth, navCtrl) {
                                isLoading = false
                            }
                        }) {
                            Text("Register with Email")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sign In Button for Google
                        Button(onClick = {
                            isLoading = true
                            val googleSignInClient = GoogleSignIn.getClient(
                                context,
                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(context.getString(R.string.default_web_client_id))
                                    .requestEmail()
                                    .build()
                            )
                            val signInIntent = googleSignInClient.signInIntent
                            launcher.launch(signInIntent)
                        }) {
                            Text("Sign In/Register with Google")
                        }
                    } else {
                        // Display User Info and Sign Out Button
                        Text(text = "Welcome, ${currentUser.displayName ?: "User"}")
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = {
                            auth.signOut()
                            Toast.makeText(context, "Signed Out Successfully", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Sign Out")
                        }
                    }
                }
            }
        }
    }
}

private fun firebaseAuthWithGoogle(account: GoogleSignInAccount, auth: FirebaseAuth, onResult: (Boolean) -> Unit) {
    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("SignInScreen", "signInWithCredential:success")
                onResult(true)
            } else {
                Log.w("SignInScreen", "signInWithCredential:failure", task.exception)
                onResult(false)
            }
        }
}

private fun emailSignIn(email: String, password: String, auth: FirebaseAuth, navCtrl: NavController, onComplete: () -> Unit) {
    if (email.isNotEmpty() && password.isNotEmpty()) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SignInScreen", "signInWithEmail:success")
                    navigateToHome(navCtrl)
                } else {
                    Log.w("SignInScreen", "signInWithEmail:failure", task.exception)
                    Toast.makeText(navCtrl.context, "Sign In Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
                onComplete()
            }
    } else {
        Log.w("SignInScreen", "Email and password must not be empty")
        Toast.makeText(navCtrl.context, "Email and password must not be empty", Toast.LENGTH_SHORT).show()
        onComplete()
    }
}

private fun emailRegister(email: String, password: String, auth: FirebaseAuth, navCtrl: NavController, onComplete: () -> Unit) {
    if (email.isNotEmpty() && password.isNotEmpty()) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SignInScreen", "createUserWithEmail:success")
                    navigateToHome(navCtrl)
                } else {
                    Log.w("SignInScreen", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(navCtrl.context, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
                onComplete()
            }
    } else {
        Log.w("SignInScreen", "Email and password must not be empty")
        Toast.makeText(navCtrl.context, "Email and password must not be empty", Toast.LENGTH_SHORT).show()
        onComplete()
    }
}

private fun navigateToHome(navCtrl: NavController) {
    navCtrl.navigate(EnumScreens.HOME.route) {
        popUpTo(EnumScreens.HOME.route) { inclusive = true }
    }
}