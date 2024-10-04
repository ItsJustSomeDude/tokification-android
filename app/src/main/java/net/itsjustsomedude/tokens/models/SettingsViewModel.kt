package net.itsjustsomedude.tokens.models

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.NotificationService
import net.itsjustsomedude.tokens.store.PreferencesRepository

class SettingsViewModel(
    private val prefsRepo: PreferencesRepository
) : ViewModel() {

    val serviceEnabled = prefsRepo.serviceEnable.getStateFlow(viewModelScope)
    fun setServiceEnabled(ctx: Context, newState: Boolean) =
        viewModelScope.launch {
            if (newState)
                NotificationService.enableService(ctx)
            else
                NotificationService.disableService(ctx)
        }

    val autoDismiss = prefsRepo.autoDismiss.getStateFlow(viewModelScope)
    fun setAutoDismiss(newState: Boolean) =
        viewModelScope.launch {
            prefsRepo.autoDismiss.setValue(newState)
        }

    val noteDebugger = prefsRepo.notificationDebugger.getStateFlow(viewModelScope)
    fun setNoteDebugger(newState: Boolean) =
        viewModelScope.launch {
            prefsRepo.notificationDebugger.setValue(newState)
        }

    val playerName = prefsRepo.playerName.getStateFlow(viewModelScope)
    fun setPlayerName(newState: String) =
        viewModelScope.launch {
            prefsRepo.playerName.setValue(newState)
        }

    val defaultCoopMode = prefsRepo.defaultCoopMode.getStateFlow(viewModelScope)
    fun setDefaultCoopMode(newState: Int) {
        viewModelScope.launch {
            prefsRepo.defaultCoopMode.setValue(newState)
        }
    }
}