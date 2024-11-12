package net.itsjustsomedude.tokens

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.db.EventRepository
import net.itsjustsomedude.tokens.reader.ShortNotification
import net.itsjustsomedude.tokens.reader.toEvent
import net.itsjustsomedude.tokens.store.PreferencesRepository
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

private const val TAG = "NotificationReader"

class NotificationService : NotificationListenerService(), KoinComponent {
	private val eventRepo: EventRepository by inject()
	private val prefsRepo: PreferencesRepository by inject()

	private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
	private val dismissHandler = Handler(Looper.getMainLooper())

	// TODO: usage of SharedPrefs.
	private val dismissDelay: Long
		get() = 10 * 1000

	companion object {
		@Volatile
		var instance: NotificationService? = null
			private set

		// StateFlow to track the running state of the service
		private val _isServiceRunning = MutableStateFlow(false)
		val isServiceRunning = _isServiceRunning.asStateFlow()

		private fun isPermissionGranted(ctx: Context): Boolean {
			val cn = ComponentName(ctx, NotificationService::class.java)
			val flat = Settings.Secure.getString(ctx.contentResolver, "enabled_notification_listeners")

			return flat != null && flat.contains(cn.flattenToString())
		}

		private fun requestPermission(
			ctx: Context,
			message: String = ctx.getString(R.string.enable_service_toast)
		) {
			Toast.makeText(ctx, message, Toast.LENGTH_LONG).show()

			ctx.startActivity(
				Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
			)
		}

		fun requestStart(ctx: Context) {
			Log.i(TAG, "Requesting Start...")
			// Older Android: If service is disabled, permission will be denied, so we won't hit this.
			if (isPermissionGranted(ctx)) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
					requestRebind(ComponentName(ctx, NotificationService::class.java))
			} else {
				requestPermission(ctx)
			}
		}

		fun requestStop() = instance?.requestStop()

		private fun requestPermissionDisable(ctx: Context) = requestPermission(ctx, "Disable it.")

		fun processAllNotifications() =
			instance?.processAllNotifications()
	}

	private fun requestStop() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
			requestUnbind()
		else if (isPermissionGranted(this))
			requestPermissionDisable(this)
	}

	override fun onCreate() {
		super.onCreate()
		instance = this
	}

	override fun onListenerConnected() {
		Log.i(TAG, "Listener Connecting...")
		_isServiceRunning.value = true

		// On boot, android will try to bind the listener, and we need to stop it if the user
		// doesn't want the service running.
		// Theoretically, on lower android versions this will never run, because "stopped" is "disabled."
		serviceScope.launch {
			if (!prefsRepo.serviceEnable.getValue())
				requestStop()
		}
	}

	override fun onNotificationPosted(sbn: StatusBarNotification) {
		serviceScope.launch {
			val note = ShortNotification(sbn)

			processNotification(note)
		}
	}

	private suspend fun processNotification(
		note: ShortNotification,
	) {
		val event = note.toEvent() ?: return

		if (eventRepo.exists(event.coop, event.kevId, event.notification)) {
			Log.i(TAG, "Skipping notification that has already been processed.")
			scheduleRemoval(note.key)
			return
		}

		eventRepo.insert(event)
		scheduleRemoval(note.key)

		updateInferredCoopValues(event)
	}

	private suspend fun scheduleRemoval(key: String) {
		if (prefsRepo.autoDismiss.getValue())
			dismissHandler.postDelayed(
				{ cancelNotification(key) },
				dismissDelay
			)
	}

	private fun processAllNotifications() {
		serviceScope.launch {
			for (sbn in activeNotifications) {
				val sbnData = ShortNotification(sbn)
				processNotification(sbnData)
			}
		}
	}

	override fun onListenerDisconnected() {
		super.onListenerDisconnected()
		destroy()
	}

	override fun onDestroy() {
		super.onDestroy()
		destroy()
	}

	private fun destroy() {
		Log.i(TAG, "Destroying Listener...")
		_isServiceRunning.value = false
		serviceScope.cancel()
		dismissHandler.removeCallbacksAndMessages(null)
		instance = null
	}
}
