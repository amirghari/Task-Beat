package com.example.taskbeat.ui.screens

import TopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.dataStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.taskbeat.data.DataRepository
import com.example.taskbeat.model.ParliamentMemberLocal
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider
import com.example.taskbeat.ui.viewmodels.NoteViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun NoteScreen(
    navCtrl: NavController,
    noteVM: NoteViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val navBackStackEntry = navCtrl.currentBackStackEntryAsState()
    val memberLocal: ParliamentMemberLocal by noteVM.memberLocal.collectAsState()
    var noteText by remember { mutableStateOf("") }

    LaunchedEffect(navBackStackEntry) { noteVM.getData() }
    LaunchedEffect(memberLocal) { noteText = memberLocal.note ?: ""}

    Scaffold(
        topBar = {
            TopBar(
                title = "Note",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            TextField(
                value = noteText,
                onValueChange = { noteText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                label = { Text("Edit Note") }
            )

            if (noteText != memberLocal.note && !noteText.isNullOrEmpty()) {
                Button(
                    onClick = {
                        noteVM.saveNote(memberLocal.hetekaId, noteText)
                        navCtrl.navigateUp()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary)
                ) {
                    Text(
                        text = "Save",
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }

            if (!memberLocal.note.isNullOrEmpty()) {
                Button(
                    onClick = {
                        noteVM.deleteNote(memberLocal.hetekaId)
                        navCtrl.navigateUp()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                ) {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            }
        }
    }
}