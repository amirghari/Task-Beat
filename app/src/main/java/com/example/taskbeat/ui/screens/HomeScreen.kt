package com.example.taskbeat.ui.screens

import TopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taskbeat.data.DataRepository
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.example.taskbeat.ui.viewmodels.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navCtrl: NavController,
    homeVM: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val displayList: List<String> by homeVM.displayList.collectAsState()
    val type: String by homeVM.type.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                title = "",
                canNavigateBack = false,
                onNavigateUp = { navCtrl.navigateUp() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (type == "party")
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { homeVM.setSortType("party") }
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Party",
                        color = if (type == "party")
                                MaterialTheme.colorScheme.onTertiary
                            else
                                MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (type == "constituency")
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { homeVM.setSortType("constituency") }
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Constituency",
                        color = if (type == "constituency")
                                MaterialTheme.colorScheme.onTertiary
                            else
                                MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
            ) {
                items(displayList) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { navCtrl.navigate(EnumScreens.MEMBERLIST.withParams(type, it.toString())) }
                            .background(MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(16.dp))
                    ) {
                        Text(
                            text = when (type) {
                                "party" -> it.uppercase()
                                "constituency" -> it.replaceFirstChar { char -> char.uppercase() }.replace("-", "-\n")
                                else -> it
                            },
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.align(Alignment.Center).padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}