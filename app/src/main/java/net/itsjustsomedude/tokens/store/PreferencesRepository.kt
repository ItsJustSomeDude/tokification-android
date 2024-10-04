package net.itsjustsomedude.tokens.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PreferencesRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        const val DEFAULT_COOP_MODE_SINK = 1
        const val DEFAULT_COOP_MODE_NORMAL = 2

        private val KEY_SERVICE_ENABLE = booleanPreferencesKey("service_enable")
        private val KEY_SELECTED_COOP = longPreferencesKey("selected_coop")
        private val KEY_PLAYER_NAME = stringPreferencesKey("player_name")
        private val KEY_AUTO_DISMISS_DELAY = longPreferencesKey("auto_dismiss_delay")
        private val KEY_AUTO_DISMISS = booleanPreferencesKey("auto_dismiss")
        private val KEY_DEFAULT_COOP_MODE = intPreferencesKey("default_coop_mode")
        private val KEY_DEBUGGER = booleanPreferencesKey("enable_notification_debugger")
    }

    val serviceEnable = StoreItem(dataStore, KEY_SERVICE_ENABLE, false)
    val selectedCoop = StoreItem(dataStore, KEY_SELECTED_COOP, 0)
    val playerName = StoreItem(dataStore, KEY_PLAYER_NAME, "Player")
    val autoDismissDelay = StoreItem(dataStore, KEY_AUTO_DISMISS_DELAY, 10L)
    val autoDismiss = StoreItem(dataStore, KEY_AUTO_DISMISS, false)
    val defaultCoopMode = StoreItem(dataStore, KEY_DEFAULT_COOP_MODE, DEFAULT_COOP_MODE_SINK)
    val notificationDebugger = StoreItem(dataStore, KEY_DEBUGGER, false)

    class StoreItem<T>(
        private val dataStore: DataStore<Preferences>,
        private val key: Preferences.Key<T>,
        private val defaultValue: T
    ) {
        fun getFlow(): Flow<T> = dataStore.data
            .map { preferences -> preferences[key] ?: defaultValue }

        fun getStateFlow(scope: CoroutineScope): StateFlow<T> = getFlow()
            .stateIn(scope, SharingStarted.Eagerly, defaultValue)

        suspend fun getValue(): T =
            getFlow().first()

        suspend fun setValue(newValue: T) = dataStore.edit { settings ->
            settings[key] = newValue
        }
    }
}

