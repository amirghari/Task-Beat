package com.example.rhythmplus

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.rhythmplus.data.AppContainer
import com.example.rhythmplus.data.AppDataContainer
//import com.example.rhythmplus.workers.CustomWorkerFactory
//import com.example.rhythmplus.workers.FetchAndUpdateDBWorker

private const val DARK_THEME_PREFERENCE_NAME = "dark_theme_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = DARK_THEME_PREFERENCE_NAME
)

class RhythmPlusApplication: Application(), Configuration.Provider {
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