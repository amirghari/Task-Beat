package com.example.taskbeat.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.taskbeat.R
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider
import com.example.taskbeat.ui.viewmodels.SignInViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException


@Composable
fun SignInScreen(
    navCtrl: NavController,
    signInVM: SignInViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val currentUser by signInVM.currentUser.collectAsState()
    val isLoading by signInVM.isLoading.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Launcher to start Google sign-in process
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let {
                signInVM.signInWithGoogle(it, context) { success ->
                    if (success) {
                        navigateToHome(navCtrl)
                    }
                }
            }
        } catch (e: ApiException) {
            Log.w("SignInScreen", "Google sign in failed", e)
            Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
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
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation() // Hides the input characters
                        )
                        Spacer(modifier = Modifier.height(56.dp))

                        // Sign In Button for Google

                        Spacer(modifier = Modifier.height(16.dp))
                        GoogleSignInButton(onClick = {
                            val googleSignInClient = GoogleSignIn.getClient(context, signInVM.getGoogleSignInOptions(context))
                            val signInIntent = googleSignInClient.signInIntent
                            launcher.launch(signInIntent)
                        })

                        // Sign In Button for Email/Password

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = {
                            signInVM.registerWithEmail(email, password, context) { success ->
                                if (success) navigateToHome(navCtrl)
                            }
                        }, modifier = Modifier.width(245.dp)) {
                            Text("Register with Email")
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            signInVM.signInWithEmail(email, password, context) { success ->
                                if (success) navigateToHome(navCtrl)
                            }
                        }, modifier = Modifier.width(245.dp))
                        {
                            Text("Sign In with Email")
                        }

                        // Register Button for New Users




                    } else {
                        // Display User Info and Sign Out Button
                        if (currentUser!!.photoUrl != null) {
                            val photoUrl = currentUser!!.photoUrl
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "User Profile Picture",
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else {
                            Image(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = "User Profile Picture",
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(26.dp))

                        Text(text = "Welcome, ${currentUser!!.displayName ?: currentUser!!.email}")
                        Spacer(modifier = Modifier.height(26.dp))

                        Button(onClick = {
                            signInVM.signOut()
                            Toast.makeText(context, "Signed Out Successfully", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Sign Out")
                        }
                        Spacer(modifier = Modifier.height(106.dp))

                    }
                }
            }
        }
    }
}

private fun navigateToHome(navCtrl: NavController) {
    navCtrl.navigate(EnumScreens.HOME.route) {
        popUpTo(EnumScreens.HOME.route) { inclusive = true }
    }
}
@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Sign In with Google")
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.google_icon),
                contentDescription = "Google Icon",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}