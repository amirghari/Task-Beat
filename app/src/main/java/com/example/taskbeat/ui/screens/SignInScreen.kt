import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taskbeat.ui.screens.EnumScreens
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider
import com.example.taskbeat.ui.viewmodels.SignInViewModel

@Composable
fun SignInScreen(
    navCtrl: NavController,
    signInVM: SignInViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
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
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sign In Screen")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    // Navigate back to home after sign-in
                    navCtrl.navigate(EnumScreens.HOME.route) {
                        popUpTo(EnumScreens.HOME.route) { inclusive = true }
                    }
                }) {
                    Text("Sign In")
                }
            }
        }
    }
}

