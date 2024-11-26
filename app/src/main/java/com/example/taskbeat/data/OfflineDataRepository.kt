package com.example.taskbeat.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.taskbeat.database.AppDatabase
import com.example.taskbeat.model.User
import com.example.taskbeat.model.Health
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OfflineDataRepository(
    context: Context,
    private val dataStore: DataStore<Preferences>
) : DataRepository {

    private companion object {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    }

    override val isDarkThemeFlow: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e("OfflineDataRepository", "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_DARK_THEME] ?: true
        }

    override suspend fun toggleTheme() {
        dataStore.edit { preferences ->
            val currentTheme = preferences[IS_DARK_THEME] ?: true
            preferences[IS_DARK_THEME] = !currentTheme
            Log.d("OfflineDataRepository", "Theme toggled: ${preferences[IS_DARK_THEME]}")
        }
    }

    private val appDatabase = AppDatabase.getDatabase(context)
    private val userDao = appDatabase.userDao()
    private val healthDao = appDatabase.healthDao()


    // User-related methods
    override suspend fun addUser(user: User) = userDao.insertUser(user)
    override fun getUserByEmail(email: String): Flow<User?> = userDao.getUserByEmail(email)
    override suspend fun deleteUserByEmail(email: String) = userDao.deleteUserByEmail(email)

    // Health data-related methods
    override suspend fun addOrUpdateHealthData(health: Health) = healthDao.insertHealthData(health)
    override fun getHealthDataByUserId(userId: Long): Flow<Health?> =
        healthDao.getHealthDataByUserId(userId)

    override suspend fun deleteHealthDataByUserId(userId: Long) =
        healthDao.deleteHealthDataByUserId(userId)

    override suspend fun updateWaterIntake(userId: Long, waterIntake: Int) {
        healthDao.updateWaterIntake(userId, waterIntake)
    }

    override suspend fun updateBMI(userId: Long, bmi: Double) {
        healthDao.updateBMI(userId, bmi)
    }
}