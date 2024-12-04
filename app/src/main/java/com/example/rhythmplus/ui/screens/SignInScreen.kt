package com.example.rhythmplus.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rhythmplus.R
import com.example.rhythmplus.ui.viewmodels.AppViewModelProvider
import com.example.rhythmplus.ui.viewmodels.SignInViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun SignInScreen(
    navCtrl: NavController,
    signInVM: SignInViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val isPrefDarkTheme by signInVM.isDarkTheme.collectAsState()
    val currentUser by signInVM.currentUser.collectAsState()
    val isLoading by signInVM.isLoading.collectAsState()
    val userAge by signInVM.userAge.collectAsState()
    val userGender by signInVM.userGender.collectAsState()
    val userDisplayName by signInVM.userDisplayName.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // State to track if we need additional information
    var showAdditionalInfoDialog by remember { mutableStateOf(false) }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var genderExpanded by remember { mutableStateOf(false) }

    // Fetch user data when the screen loads
    LaunchedEffect(currentUser) {
        currentUser?.email?.let { email ->
            signInVM.loadUserFromLocalDatabase(email) { user ->
                if (user == null) {
                    // Handle case when user is not found in the local database
                    Log.d("SignInScreen", "User not found in the local database.")
                } else {
                    Log.d("SignInScreen", "User loaded successfully from the local database.")
                }
            }
        }
    }

    // Launcher to start Google sign-in process
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let {
                displayName = account.displayName ?: "User"
                signInVM.signInWithGoogle(it, context) { needsAdditionalInfo ->
                    showAdditionalInfoDialog = needsAdditionalInfo
                }
            }
        } catch (e: ApiException) {
            Log.w("SignInScreen", "Google sign in failed", e)
            Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
//        topBar = {
//            TopBar(
//                title = "Sign In",
//                canNavigateBack = true,
//                onNavigateUp = { navCtrl.navigateUp() }
//            )
//        }
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
                        Spacer(modifier = Modifier.height(16.dp))

                        // Sign In Button for Google
                        GoogleSignInButton(onClick = {
                            val googleSignInClient = GoogleSignIn.getClient(context, signInVM.getGoogleSignInOptions(context))
                            val signInIntent = googleSignInClient.signInIntent
                            launcher.launch(signInIntent)
                        })

                        Spacer(modifier = Modifier.height(16.dp))

                        // Register Button for New Users
                        // Register Button for New Users
                        Button(onClick = {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                signInVM.registerWithEmail(email, password, context) { success ->
                                    if (success) {
                                        showAdditionalInfoDialog = true
                                    } else {
                                        Toast.makeText(context, "Registration Failed. Please try again.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Please fill in both email and password", Toast.LENGTH_SHORT).show()
                            }
                        }, modifier = Modifier.width(245.dp)) {
                            Text("Register with Email")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sign In Button for Email/Password
                        Button(onClick = {
                            signInVM.signInWithEmail(email, password, context) { success ->
                                if (success) navigateToHome(navCtrl)
                            }
                        }, modifier = Modifier.width(245.dp)) {
                            Text("Sign In with Email")
                        }
                    } else {
                        // Display User Info and Sign Out Button
                        Spacer(modifier = Modifier.height(100.dp))
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
                        } else {
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

                        Spacer(modifier = Modifier.height(40.dp))

                        Text(
                            text = "Welcome, $userDisplayName",
                            fontSize = 24.sp, // Adjust the size as needed
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Display User's Age and Gender
                        if (userAge != null && userGender != null) {

                            Text(text = "Gender: $userGender")
                            Text(text = "Age: $userAge")
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color.Transparent),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Switch(
                                checked = isPrefDarkTheme,
                                onCheckedChange = { signInVM.toggleTheme() },
                                modifier = Modifier.padding(end = 16.dp),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(146.dp))


                        Button(onClick = {
                            signInVM.signOut()
                            Toast.makeText(context, "Signed Out Successfully", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Sign Out")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(246.dp))


                // Dialog to collect additional user info (age and gender)
                if (showAdditionalInfoDialog) {
                    AlertDialog(
                        onDismissRequest = { showAdditionalInfoDialog = false },
                        title = { Text("Additional Information") },
                        text = {
                            Column {
                                TextField(
                                    value = age,
                                    onValueChange = { age = it },
                                    label = { Text("Age") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                // Dropdown Menu for Gender Selection

                                TextField(
                                    value = displayName,
                                    onValueChange = { displayName = it },
                                    label = { Text("Display Name") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))


                                Box {
                                    OutlinedButton(onClick = { genderExpanded = !genderExpanded }) {
                                        Text(text = if (gender.isEmpty()) "Select Gender" else gender)
                                    }
                                    DropdownMenu(
                                        expanded = genderExpanded,
                                        onDismissRequest = { genderExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Male") },
                                            onClick = {
                                                gender = "Male"
                                                genderExpanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Female") },
                                            onClick = {
                                                gender = "Female"
                                                genderExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (age.isNotBlank() && gender.isNotBlank() && displayName.isNotBlank()) {
                                        signInVM.saveAdditionalUserInfo(
                                            email = currentUser?.email ?: email,
                                            password = "", // Password not required for Google Sign-In
                                            age = age.toInt(),
                                            gender = gender,
                                            displayName = displayName
                                        )
                                        showAdditionalInfoDialog = false
                                        navigateToHome(navCtrl)
                                    } else {
                                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Text("Submit")
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun navigateToHome(navCtrl: NavController) {
    navCtrl.navigate(EnumScreens.HEART_RATE.route) {
        popUpTo(EnumScreens.HEART_RATE.route) { inclusive = true }
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