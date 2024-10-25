package com.example.taskbeat.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

interface AppContainer {
    val dataRepo: DataRepository
}

class AppDataContainer(context: Context, dataStore: DataStore<Preferences>): AppContainer {
    override val dataRepo: DataRepository by lazy {
        OfflineDataRepository(context, dataStore)
    }
}