package com.example.taskbeat.ui.screens

import TopBar
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import com.example.taskbeat.R
import com.example.taskbeat.data.DataRepository
import com.example.taskbeat.model.ParliamentMember
import com.example.taskbeat.model.ParliamentMemberExtra
import com.example.taskbeat.model.ParliamentMemberLocal
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import coil.request.ImageRequest
import com.example.taskbeat.ui.components.CustomImageDisplay
import com.example.taskbeat.ui.viewmodels.MemberViewModel

@Composable
fun MemberScreen(
    navCtrl: NavController,
    memberVM: MemberViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val imgBaseUrl = "https://avoindata.eduskunta.fi/"

    val context = LocalContext.current
    val navBackStackEntry = navCtrl.currentBackStackEntryAsState()
    val scrollState = rememberScrollState()

    val member: ParliamentMember by memberVM.member.collectAsState()
    val memberExtra: ParliamentMemberExtra by memberVM.memberExtra.collectAsState()
    val memberLocal: ParliamentMemberLocal by memberVM.memberLocal.collectAsState()

    val boxColor = MaterialTheme.colorScheme.tertiaryContainer
    val onBoxColor = MaterialTheme.colorScheme.onTertiaryContainer

    LaunchedEffect(navBackStackEntry) { memberVM.getData() }

    Scaffold(
        topBar = {
            TopBar(
                title = "",
                canNavigateBack = true,
                onNavigateUp = { navCtrl.navigateUp() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .height(500.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                CustomImageDisplay(
                    context = context,
                    imageUrl = "$imgBaseUrl${member.pictureUrl}",
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .align(Alignment.BottomStart)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 1f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 15.dp)
                ) {
                    if (member.minister) {
                        Text(
                            text = "Minister",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    Text(
                        text = "${member.firstname} ${member.lastname}",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(boxColor)
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Heteka ID ",
                            color = onBoxColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${member.hetekaId}",
                            color = onBoxColor,
                            fontSize = 14.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(boxColor)
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Seat",
                            color = onBoxColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${member.seatNumber}",
                            color = onBoxColor,
                            fontSize = 14.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(boxColor)
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Party",
                            color = onBoxColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${member.party.uppercase()}",
                            color = onBoxColor,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(boxColor)
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Born Year",
                            color = onBoxColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (memberExtra.bornYear == 0) "No data" else "${memberExtra.bornYear}",
                            color = onBoxColor,
                            fontSize = 14.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(boxColor)
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Constituency",
                            color = onBoxColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (memberExtra.constituency == "") "No data" else "${memberExtra.constituency}",
                            color = onBoxColor,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (!memberExtra.twitter.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(boxColor)
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Twitter",
                            color = onBoxColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(memberExtra.twitter))
                                    context.startActivity(intent)
                                }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.x_logo),
                                contentDescription = "Twitter logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().padding(8.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(boxColor)
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Mark as favorite",
                        color = onBoxColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    when (memberLocal.favorite) {
                        true -> Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Favorite icon",
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp).clickable {
                                memberVM.changeFavorite(memberLocal.hetekaId)
                            }
                        )
                        false -> Icon(
                            imageVector = Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite icon",
                            tint = onBoxColor,
                            modifier = Modifier.size(24.dp).clickable {
                                memberVM.changeFavorite(memberLocal.hetekaId)
                            }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(boxColor)
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Note",
                            color = onBoxColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                navCtrl.navigate(EnumScreens.NOTE.withParams(member.hetekaId.toString()))
                            }
                        ) {
                            Icon(
                                imageVector = if (memberLocal.note.isNullOrEmpty()) { Icons.Filled.Add } else { Icons.Filled.Edit },
                                contentDescription = "Add icon",
                                tint = onBoxColor,
                            )
                        }
                    }

                    if (!memberLocal.note.isNullOrEmpty()) {
                        HorizontalDivider(
                            color = Color.Black,
                            thickness = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = memberLocal.note!!,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}