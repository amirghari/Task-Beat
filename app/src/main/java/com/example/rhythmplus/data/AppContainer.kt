package com.example.rhythmplus.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

interface AppContainer {
    val dataRepo: DataRepository
    val model: Gemma22BModel
}

class AppDataContainer(context: Context, dataStore: DataStore<Preferences>): AppContainer {
    override val dataRepo: DataRepository by lazy {
        OfflineDataRepository(context, dataStore)
    }

    override val model: Gemma22BModel by lazy {
        Gemma22BModel.getInstance(context)
    }
}