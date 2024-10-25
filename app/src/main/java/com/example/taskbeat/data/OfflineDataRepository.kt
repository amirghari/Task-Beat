package com.example.taskbeat.data

import android.content.Context
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.taskbeat.database.DataDatabase
import com.example.taskbeat.model.ParliamentMember
import com.example.taskbeat.model.ParliamentMemberExtra
import com.example.taskbeat.model.ParliamentMemberLocal
import com.example.taskbeat.network.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class OfflineDataRepository(
    context: Context,
    private val dataStore: DataStore<Preferences>
): DataRepository {
    private companion object {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    }

    override val isDarkThemeFlow: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e("DBG", "Error reading preferences.", it)
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
            Log.d("DBG", "pref ${preferences[IS_DARK_THEME]}")
        }
    }

    private val retrofitApi = RetrofitInstance.api
    private val dataDao = DataDatabase.getDatabase(context).dataDao()

    override suspend fun addParliamentMember(data: ParliamentMember) = dataDao.addParliamentMember(data)
    override suspend fun addParliamentMemberExtra(data: ParliamentMemberExtra) = dataDao.addParliamentMemberExtra(data)

    override fun getAllParliamentMembers(): Flow<List<ParliamentMember>> = dataDao.getAllParliamentMembers()
    override fun getAllParliamentMembersExtra(): Flow<List<ParliamentMemberExtra>> = dataDao.getAllParliamentMembersExtra()

    override suspend fun fetchParliamentMembersData() = retrofitApi.getParliamentMembers()
    override suspend fun fetchParliamentMembersExtraData() = retrofitApi.getParliamentMembersExtras()

    override fun getMemberWithId(id: Int): Flow<ParliamentMember> = dataDao.getMemberWithId(id)
    override fun getMemberExtraWithId(id: Int): Flow<ParliamentMemberExtra> = dataDao.getMemberExtraWithId(id)
    override fun getMemberLocalWithId(id: Int): Flow<ParliamentMemberLocal?> = dataDao.getMemberLocalWithId(id)

    override fun getParties(): Flow<List<String>> = dataDao.getParties()
    override fun getConstituencies(): Flow<List<String>> = dataDao.getConstituencies()
    override fun getAllPMWithParty(party: String): Flow<List<ParliamentMember>> = dataDao.getAllPMWithParty(party)
    override fun getAllPMWithConstituency(constituency: String): Flow<List<ParliamentMember>> = dataDao.getAllPMWithConstituency(constituency)

    override suspend fun addParliamentLocal(data: ParliamentMemberLocal) = dataDao.addParliamentLocal(data)
    override fun getAllPMIds(): Flow<List<Int>> = dataDao.getAllPMIds()

    override suspend fun updateNoteWithId(id: Int, note: String?) = dataDao.updateNoteWithId(id, note)
    override suspend fun deleteNoteWithId(id: Int) = dataDao.deleteNoteWithId(id)

    override fun getFavoriteById(id: Int): Flow<Boolean> = dataDao.getFavoriteById(id)
    override suspend fun toggleFavorite(id: Int) = dataDao.toggleFavorite(id)
}