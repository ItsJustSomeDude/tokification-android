package net.itsjustsomedude.tokens.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import net.itsjustsomedude.tokens.dataStore

class StoreRepo(context: Context) {
    private val dataStore: DataStore<Preferences> = context.dataStore
    
    val selectedCoopState: Flow<Long> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_COOP] ?: 0
        }

    suspend fun setSelectedCoop(newValue: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_COOP] = newValue
        }
    }

    suspend fun getSelectedCoop() =
        selectedCoopState.first()
}