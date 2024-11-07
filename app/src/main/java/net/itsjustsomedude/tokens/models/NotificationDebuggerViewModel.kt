package net.itsjustsomedude.tokens.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import net.itsjustsomedude.tokens.NotificationHelper

class NotificationDebuggerViewModel(
	private val notificationHelper: NotificationHelper
) : ViewModel() {
	var player = mutableStateOf("")
	var coop = mutableStateOf("")
	var kevId = mutableStateOf("")

	fun sendNotification(isCR: Boolean) {
		notificationHelper.sendFake(
			player.value,
			coop.value,
			kevId.value,
			isCR
		)
	}
}