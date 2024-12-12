package com.example.taskbeat.ui.screens.homescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taskbeat.R
import com.example.taskbeat.ui.screens.EnumScreens
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider
import com.example.taskbeat.ui.viewmodels.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    navCtrl: NavController,
    homeVM: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
        ) {

            // Static header or content before scrollable items
            item {
                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                       , // Ensure minimal padding ,
                    verticalAlignment = Alignment.CenterVertically
                ){

                    Box(
                        modifier = Modifier
                            .weight(2.0f) // Increase weight for wider box
                            // Add spacing between items
                    ) {
                        HomeItemBox(
                            title = "Heart",
                            description = "",
                            iconResId = R.drawable.heart_image
                        ) {
                            navCtrl.navigate(EnumScreens.HEART_RATE.route)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(2.0f) // Increase weight for wider box
                             // Add spacing between items
                    ) {
                        HomeItemBox(
                            title = "Chat",
                            description = "",
                            iconResId = R.drawable.chat
                        ) {
                            navCtrl.navigate(EnumScreens.LOADING_CHAT.route)
                        }
                    }
                }
            }

            // Dynamic list of home items
            items(
                listOf(
                    Triple("Water", "1234", R.drawable.water_bottle),
                    Triple("Body Composition", "123", R.drawable.bc),
                    Triple("Blood Pressure", "123", R.drawable.blood_pressure_gauge),
                    Triple("Blood Glucose", "123", R.drawable.blood_glucose_icon)
                )
            ) { (title, description, iconResId) ->
                HomeItemBox(
                    title = title,
                    description = description,
                    iconResId = iconResId
                ) {
                    when (title) {
                        "Water" -> navCtrl.navigate(EnumScreens.WATER.route)
                        "Body Composition" -> navCtrl.navigate(EnumScreens.BODY_COMPOSITION.route)
                        "Blood Pressure" -> navCtrl.navigate(EnumScreens.BLOOD_PRESSURE.route)
                        "Blood Glucose" -> navCtrl.navigate(EnumScreens.BLOOD_GLUCOSE.route)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeItemBox(
    title: String,
    description: String,
    iconResId: Int,
    navigateToScreen: () -> Unit
) {
    Spacer(modifier = Modifier.height(2.dp))

    Box(
        modifier = Modifier
            .height(150.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp) // Consistent spacing
            .clip(RoundedCornerShape(16.dp))
            .clickable { navigateToScreen() }
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(20.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp) // Add padding as needed
            ) {
                // Centered vertically on the left
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart // Align text to the start (left) and center vertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 20.sp, // Adjust font size as needed
                    )
                }

                // Bottom-end aligned image
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd // Align image to the bottom-end
                ) {
                    Image(
                        painter = painterResource(id = iconResId),
                        contentDescription = "$title Icon",
                        modifier = Modifier
                            .size(81.dp), // Adjust size as needed
                        contentScale = ContentScale.Crop
                    )
                }
            }

        }
    }
}