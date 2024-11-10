package com.example.taskbeat.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface AppContainer {
    val dataRepo: DataRepository
    var model: Gemma22BModel
}

class AppDataContainer(context: Context, dataStore: DataStore<Preferences>): AppContainer {
    override val dataRepo: DataRepository by lazy {
        OfflineDataRepository(context, dataStore)
    }

    override lateinit var model: Gemma22BModel

    init { createModelInstance(context) }

    private fun createModelInstance(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                model = Gemma22BModel.getInstance(context)
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
            }
        }
    }
}