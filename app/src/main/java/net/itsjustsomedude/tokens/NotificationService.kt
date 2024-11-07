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
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.db.EventRepository
import net.itsjustsomedude.tokens.reader.ShortNotification
import net.itsjustsomedude.tokens.reader.toEvent
import net.itsjustsomedude.tokens.store.PreferencesRepository
import org.koin.android.ext.android.inject
import org.koin.mp.KoinPlatform.getKoin

private const val TAG = "NotificationReader"

class NotificationService : NotificationListenerService() {
	private val eventRepo: EventRepository by inject()

	private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
	private var dismissHandler: Handler? = null

	override fun onListenerConnected() {

		instance = this
		dismissHandler = Handler(Looper.getMainLooper())

		// On boot, android will try to bind the listener, and we need to stop it if the user
		// doesn't want the service running.
		// Theoretically, on lower android versions this will never run, because "stopped" is "disabled."
		serviceScope.launch {
			if (!isServiceEnabled())
				disableService(this@NotificationService)
		}

		println("Connected!")
	}

	override fun onListenerDisconnected() {
		super.onListenerDisconnected()
		destroy()
		println("Disconnected.")
	}

	override fun onDestroy() {
		super.onDestroy()
		destroy()
		println("Destroyed.")
	}

	private fun destroy() {
		serviceScope.cancel()
		if (dismissHandler != null) {
			dismissHandler!!.removeCallbacksAndMessages(null)
			dismissHandler = null
		}
		instance = null
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
		println("Processing $note")
		val event = note.toEvent() ?: return
		println("Valid Notification, adding.")

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
		val shouldDismiss = shouldDismiss()

		println("Dismiss? $shouldDismiss")
		if (shouldDismiss)
			dismissHandler!!.postDelayed(
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

	private suspend fun shouldDismiss() =
		getKoin().get<PreferencesRepository>().autoDismiss.getValue()

	// TODO: usage of SharedPrefs.
	private val dismissDelay: Long
		get() = 10 * 1000

	companion object {
		var instance: NotificationService? = null
			private set

		fun processAllNotifications() =
			instance?.processAllNotifications()

//        fun isServiceRunning(): Boolean = instance != null

		suspend fun enableService(ctx: Context) {
			getKoin().get<PreferencesRepository>()
				.serviceEnable.setValue(true)

			// Older Android: If service is disabled, permission will be denied, so we won't hit this.
			if (isPermissionGranted(ctx)) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
					requestRebind(
						ComponentName(ctx, NotificationService::class.java)
					)
			} else {
				requestPermission(ctx)
			}
		}

		suspend fun disableService(ctx: Context) {
			getKoin().get<PreferencesRepository>()
				.serviceEnable.setValue(false)

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
				instance?.requestUnbind()
			else if (isPermissionGranted(ctx))
				requestPermissionDisable(ctx)
		}

		suspend fun isServiceEnabled() =
			getKoin().get<PreferencesRepository>().serviceEnable.getValue()

		private fun requestPermission(
			ctx: Context,
			message: String = ctx.getString(R.string.enable_service_toast)
		) {
			Toast.makeText(ctx, message, Toast.LENGTH_LONG).show()

			ctx.startActivity(
				Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
			)
		}

		private fun requestPermissionDisable(ctx: Context) = requestPermission(ctx, "Disable it.")

		private fun isPermissionGranted(ctx: Context): Boolean {
			val cn = ComponentName(ctx, NotificationService::class.java)
			val flat =
				Settings.Secure.getString(ctx.contentResolver, "enabled_notification_listeners")

			return flat != null && flat.contains(cn.flattenToString())
		}
	}
}

