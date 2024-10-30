import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider
import com.example.taskbeat.ui.viewmodels.HeartRateViewModel
import com.example.taskbeat.R

@Composable
fun HeartRateScreen(
    navCtrl: NavController,
    heartrateVM: HeartRateViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Scaffold is used to define the basic structure of the screen, such as the top bar and the main content area.
    Scaffold(
        topBar = {
            TopBar(
                title = "Heart Rate",
                canNavigateBack = true,
                onNavigateUp = { navCtrl.navigateUp() }
            )
        }
    ) { paddingValues ->
        // Box is used to hold the entire content, applying padding from Scaffold.
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Placeholder for the heart image representation
                Image(
                    painter = painterResource(id = R.drawable.heart_image),
                    contentDescription = "Heart Image",
                    modifier = Modifier.size(400.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(14.dp))

                // BPM Text (simulating heart rate measurement)
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(15.dp, Color(0xFF7EBD8F), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "68 BPM", // Replace with data from heartrateVM when live data is available
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                    )
                }


                Spacer(modifier = Modifier.height(32.dp))

                // Measure Button
                Button(
                    onClick = {
                        // Trigger heart rate measurement logic here
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7EBD8F)),
                    modifier = Modifier.padding(6.dp)
                ) {
                    Text(text = "Measure")
                }
            }
        }
    }
}

// Explanation:
// 1. Scaffold: Provides a consistent layout structure (topBar, content area, etc.).
// 2. TopBar: Contains the title and navigation action. Can be reused across different screens.
// 3. Box: Used to wrap the entire screen content and center-align it.
// 4. Column: Arranges child components vertically for clean UI organization.
// 5. Heart Icon Placeholder: Currently a Box with red background representing the heart graphic.
// 6. BPM Text: Displays current heart rate in bold and large font size.
// 7. Measure Button: Initiates heart rate measurement. Adjust colors and actions as needed.
