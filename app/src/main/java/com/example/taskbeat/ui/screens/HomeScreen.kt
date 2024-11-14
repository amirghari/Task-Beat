import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.taskbeat.ui.screens.EnumScreens
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider
import com.example.taskbeat.ui.viewmodels.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    navCtrl: NavController,
    homeVM: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val screens = listOf(
        EnumScreens.STEPS_COUNTER,
        EnumScreens.WORKOUT_TIME,
        EnumScreens.HEART_RATE,
        EnumScreens.BODY_COMPOSITION,
        EnumScreens.WATER,
        EnumScreens.BLOOD_PRESSURE,
        EnumScreens.BLOOD_GLUCOSE,
        EnumScreens.SETTINGS,
        EnumScreens.SIGN_IN
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Ensure the header spans the full width
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Show user profile or Sign In button based on authentication state
                if (currentUser != null) {
                    // If the user is logged in, show their profile picture or email
                    val photoUrl = currentUser.photoUrl
                    if (photoUrl != null) {
                        // Show profile picture
                        Button(
                            onClick = {
                                navCtrl.navigate(EnumScreens.SIGN_IN.route)
                            }
                        )
                            {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "User Profile Picture",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray),
                                contentScale = ContentScale.Crop
                                )
                            }
                    } else {
                        // Show email if profile picture is not available
                        Button(
                            onClick = {
                                navCtrl.navigate(EnumScreens.SIGN_IN.route)
                            }
                        )
                        {
                            Text(
                            text = currentUser.email ?: "User",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        }
                    }
                } else {
                    // If the user is not logged in, show "Sign In" button
                    Button(
                        onClick = {
                            navCtrl.navigate(EnumScreens.SIGN_IN.route)
                        }
                    ) {
                        Text("Sign In", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // TopBar navigation actions on the right
                TopBar(
                    title = "",
                    canNavigateBack = false,
                    onNavigateUp = { navCtrl.navigateUp() }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
        ) {
            items(screens) { screen ->
                HomeItemBox(screen.route) { navCtrl.navigate(screen.route) }
            }
        }
    }
}

@Composable
fun HomeItemBox(
    screen: String,
    navigateToScreen: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .size(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { navigateToScreen() }
            .background(MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(screen)
    }
}