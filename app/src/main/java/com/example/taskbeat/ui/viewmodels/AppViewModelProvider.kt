package com.example.taskbeat.ui.viewmodels

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.taskbeat.TaskBeatApplication

fun CreationExtras.pmApplication(): TaskBeatApplication =
    (this[APPLICATION_KEY] as TaskBeatApplication)

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                pmApplication().container.dataRepo
            )
        }

        initializer {
            SettingsViewModel(
                pmApplication().container.dataRepo
            )
        }

        initializer {
            RemindersListViewModel(
                this.createSavedStateHandle(),
                pmApplication().container.dataRepo,
            )
        }

        initializer {
            ReminderViewModel(
                this.createSavedStateHandle(),
                pmApplication().container.dataRepo
            )
        }

        initializer {
            HeartRateViewModel(
                this.createSavedStateHandle(),
                pmApplication().container.dataRepo
            )
        }

        initializer {
            TopBarViewModel(
                pmApplication().container.dataRepo
            )
        }
    }
}