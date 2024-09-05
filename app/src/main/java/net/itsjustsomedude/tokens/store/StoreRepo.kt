package net.itsjustsomedude.tokens.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.itsjustsomedude.tokens.dataStore

class StoreRepo(private val context: Context) {
    val selectedCoop: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_COOP] ?: 0
        }

    suspend fun setSelectedCoop(newValue: Long) {
        println("Setting it, part 1: $newValue")

        context.dataStore.edit { preferences ->
            println("Setting DS to store $newValue")
            preferences[PreferencesKeys.SELECTED_COOP] = newValue
        }
    }
}