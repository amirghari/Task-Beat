package com.example.taskbeat

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.taskbeat.data.AppContainer
import com.example.taskbeat.data.AppDataContainer
//import com.example.taskbeat.workers.CustomWorkerFactory
//import com.example.taskbeat.workers.FetchAndUpdateDBWorker
import java.util.concurrent.TimeUnit

private const val DARK_THEME_PREFERENCE_NAME = "dark_theme_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = DARK_THEME_PREFERENCE_NAME
)

class TaskBeatApplication: Application(), Configuration.Provider {
   lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this, dataStore)
        WorkManager.initialize(this, workManagerConfiguration)
//        updateDataOnCreate()
//        schedulePeriodicFetchAndUpdateWork()
    }

//    private fun updateDataOnCreate() {
//        val fetchAndUpdateDBWorkRequest = OneTimeWorkRequestBuilder<FetchAndUpdateDBWorker>()
//            .setConstraints(
//                Constraints.Builder()
//                    .setRequiredNetworkType(NetworkType.CONNECTED)
//                    .build()
//            )
//            .build()
//
//        WorkManager.getInstance(this).enqueue(fetchAndUpdateDBWorkRequest)
//    }
//
//    private fun schedulePeriodicFetchAndUpdateWork() {
//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .setRequiresBatteryNotLow(true)
//            .build()
//
//        val periodicWorkRequest = PeriodicWorkRequestBuilder<FetchAndUpdateDBWorker>(24, TimeUnit.HOURS)
//            .setConstraints(constraints)
//            .build()
//
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//            "FetchAndUpdateDBWorker",
//            ExistingPeriodicWorkPolicy.KEEP,
//            periodicWorkRequest
//        )
//    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
//            .setWorkerFactory(CustomWorkerFactory(container.dataRepo))
            .build()
}