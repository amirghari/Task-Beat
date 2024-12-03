package com.example.taskbeat.ui.screens.homescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

    Scaffold(
        // Scaffold content if needed
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 4.dp
                    ),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Example Home Boxes
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { navCtrl.navigate(EnumScreens.HEART_RATE.route) }
                        .background(
                            MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Heart Rate",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.heart_image),
                                contentDescription = "Heart Rate Icon",
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(start = 8.dp, bottom = 16.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(150.dp)
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { navCtrl.navigate(EnumScreens.LOADING_CHAT.route) }
                        .background(
                            MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Chat",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.chat),
                                contentDescription = "Chat Icon",
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(start = 8.dp, bottom = 16.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Other Home Items with Icons
            HomeItemBox(
                title = "Water",
                description = "1234",
                iconResId = R.drawable.water_bottle
            ) { navCtrl.navigate(EnumScreens.WATER.route) }

            HomeItemBox(
                title = "Body Composition",
                description = "123",
                iconResId = R.drawable.bc
            ) { navCtrl.navigate(EnumScreens.BODY_COMPOSITION.route) }

            HomeItemBox(
                title = "Blood Pressure",
                description = "123",
                iconResId = R.drawable.blood_pressure_gauge
            ) { navCtrl.navigate(EnumScreens.BLOOD_PRESSURE.route) }

            HomeItemBox(
                title = "Blood Glucose",
                description = "123",
                iconResId = R.drawable.blood_glucose_icon
            ) { navCtrl.navigate(EnumScreens.BLOOD_GLUCOSE.route) }
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
    Box(
        modifier = Modifier
            .height(150.dp)
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { navigateToScreen() }
            .background(MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = "$title Icon",
                    modifier = Modifier
                        .size(64.dp)
                        .padding(start = 8.dp, bottom = 16.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp)
                )
            }
        }
    }
}
