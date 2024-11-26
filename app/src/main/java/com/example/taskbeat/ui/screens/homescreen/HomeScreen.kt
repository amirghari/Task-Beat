package com.example.taskbeat.ui.screens.homescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
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

    val screens = listOf(
        EnumScreens.STEPS_COUNTER,
        EnumScreens.WORKOUT_TIME,
        EnumScreens.BODY_COMPOSITION,
        EnumScreens.WATER,
        EnumScreens.BLOOD_PRESSURE,
        EnumScreens.BLOOD_GLUCOSE,
    )

    Scaffold(
        // ... existing scaffold content ...
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
                            text = "Heart rate",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .height(64.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Text(
                                text = "88",
                                style = MaterialTheme.typography.displayMedium,
                                modifier = Modifier.padding(end = 8.dp)
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
                            modifier = Modifier
                                .height(64.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "Chat Picture",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Add your HomeItemBoxes here
//            HomeItemBox("Steps counter", "1234") { navCtrl.navigate(EnumScreens.STEPS_COUNTER.route) }
//            HomeItemBox("Workout time", "12:34") { navCtrl.navigate(EnumScreens.WORKOUT_TIME.route) }
            HomeItemBox("Water", "1234") { navCtrl.navigate(EnumScreens.WATER.route) }
            HomeItemBox("Body composition", "123") { navCtrl.navigate(EnumScreens.BODY_COMPOSITION.route) }
            HomeItemBox("Blood pressure", "123") { navCtrl.navigate(EnumScreens.BLOOD_PRESSURE.route) }
            HomeItemBox("Blood glucose", "123") { navCtrl.navigate(EnumScreens.BLOOD_GLUCOSE.route) }

            // Add more items if needed
        }
    }
}

@Composable
fun HomeItemBox(
    title: String,
    description: String,
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
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}