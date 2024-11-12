package net.itsjustsomedude.tokens.models

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.NotificationService
import net.itsjustsomedude.tokens.network.UpdateChecker
import net.itsjustsomedude.tokens.store.PreferencesRepository

class SettingsViewModel(
	private val prefsRepo: PreferencesRepository,
	private val updateChecker: UpdateChecker
) : ViewModel() {

	val serviceRunningState = NotificationService.isServiceRunning

	fun setServiceStatus(ctx: Context, newState: Boolean) = viewModelScope.launch {
		prefsRepo.serviceEnable.setValue(newState)

		if (newState)
			NotificationService.requestStart(ctx)
		else
			NotificationService.requestStop()
	}

	val autoDismiss = prefsRepo.autoDismiss.getStateFlow(viewModelScope)
	fun setAutoDismiss(newState: Boolean) =
		prefsRepo.autoDismiss.setValueIn(viewModelScope, newState)

	val noteDebugger = prefsRepo.notificationDebugger.getStateFlow(viewModelScope)
	fun setNoteDebugger(newState: Boolean) =
		prefsRepo.notificationDebugger.setValueIn(viewModelScope, newState)

	val playerName = prefsRepo.playerName.getStateFlow(viewModelScope)
	fun setPlayerName(newState: String) =
		prefsRepo.playerName.setValueIn(viewModelScope, newState)

	val defaultCoopMode = prefsRepo.defaultCoopMode.getStateFlow(viewModelScope)
	fun setDefaultCoopMode(newState: Int) =
		prefsRepo.defaultCoopMode.setValueIn(viewModelScope, newState)

	val sentryEnabled = prefsRepo.sentryEnabled.getStateFlow(viewModelScope)
	fun setSentryEnabled(newState: Boolean) {
		prefsRepo.sentryEnabled.setValueIn(viewModelScope, newState)
	}
}
