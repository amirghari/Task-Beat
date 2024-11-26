package com.example.taskbeat.ui.viewmodels

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.taskbeat.TaskBeatApplication
import com.example.taskbeat.ui.viewmodels.home.BloodGlucoseViewModel
import com.example.taskbeat.ui.viewmodels.home.BloodPressureViewModel
import com.example.taskbeat.ui.viewmodels.home.BodyCompositionViewModel
import com.example.taskbeat.ui.viewmodels.home.HomeViewModel
import com.example.taskbeat.ui.viewmodels.home.StepsCounterViewModel
import com.example.taskbeat.ui.viewmodels.home.WaterViewModel
import com.example.taskbeat.ui.viewmodels.home.WorkoutTimeViewModel

fun CreationExtras.pmApplication(): TaskBeatApplication =
    (this[APPLICATION_KEY] as TaskBeatApplication)

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            TopBarViewModel(
                pmApplication().container.dataRepo
            )
        }

        initializer {
            HomeViewModel(
                pmApplication().container.dataRepo
            )
        }

        initializer {
            StepsCounterViewModel(
                pmApplication().container.dataRepo
            )
        }

        initializer {
            WorkoutTimeViewModel(
                pmApplication().container.dataRepo
            )
        }

        initializer {
            HeartRateViewModel(
                pmApplication().container.dataRepo
            )
        }

        initializer {
            BodyCompositionViewModel(
                pmApplication().container.dataRepo
            )
        }

        initializer {
            WaterViewModel(
                pmApplication().container.dataRepo
            )
        }

        initializer {
            BloodPressureViewModel(
                pmApplication().container.dataRepo
            )
        }

        initializer {
            BloodGlucoseViewModel(
                pmApplication().container.dataRepo
            )
        }
        initializer {
            SignInViewModel(
                pmApplication().container.dataRepo
            )
        }

        initializer {
            ChatViewModel(
                pmApplication().container.dataRepo,
                pmApplication().container.model
            )
        }
    }
}