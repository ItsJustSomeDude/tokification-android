package net.itsjustsomedude.tokens.models

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.sentry.Sentry
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.NotificationService
import net.itsjustsomedude.tokens.network.UpdateChecker
import net.itsjustsomedude.tokens.store.PreferencesRepository

class SettingsViewModel(
    private val prefsRepo: PreferencesRepository,
    private val updateChecker: UpdateChecker
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
    fun setDefaultCoopMode(newState: Int) =
        prefsRepo.defaultCoopMode.setValueIn(viewModelScope, newState)

    val sentryEnabled = prefsRepo.sentryEnabled.getStateFlow(viewModelScope)
    fun setSentryEnabled(newState: Boolean) {
        prefsRepo.sentryEnabled.setValueIn(viewModelScope, newState)

        if (newState) {
            // TODO: No clue if this will work at all.
//            TokificationApp.startSentry()
        } else {
            Sentry.close()
        }
    }

}