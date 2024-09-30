package net.itsjustsomedude.tokens

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.db.EventRepository
import net.itsjustsomedude.tokens.reader.ShortNotification
import net.itsjustsomedude.tokens.reader.toEvent
import org.koin.android.ext.android.inject

private const val TAG = "NotificationReader"

class NotificationService : NotificationListenerService() {
    private val eventRepo: EventRepository by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var dismissHandler: Handler? = null

    override fun onListenerConnected() {
        instance = this

        // Theoretically, on lower android versions this will never run, because "stopped" is "disabled."
        if (!isServiceEnabled(this))
            disableService(this)
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
        serviceScope.cancel()
        if (dismissHandler != null) {
            dismissHandler!!.removeCallbacksAndMessages(null)
            dismissHandler = null
        }
        instance = null
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val note = ShortNotification(sbn)

        serviceScope.launch {
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
    }

    private fun scheduleRemoval(key: String) {
        if (shouldDismiss)
            dismissHandler!!.postDelayed(
                { cancelNotification(key) },
                dismissDelay.toLong()
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

    // TODO: usage of SharedPrefs.
    private val shouldDismiss: Boolean =
        try {
            PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_DISMISS, false)
        } catch (e: RuntimeException) {
            false
        }


    // TODO: usage of SharedPrefs.
    private val dismissDelay: Long
        get() = 10 * 1000

    companion object {
        const val PREF_ENABLED: String = "service_control_enable_service"
        const val PREF_DISMISS: String = "auto_dismiss"

        var instance: NotificationService? = null
            private set

        fun processAllNotifications() =
            instance?.processAllNotifications()

        val isServiceRunning: Boolean
            get() = instance != null

        fun enableService(ctx: Context) {
            // TODO: Usage of SharedPrefs
            PreferenceManager.getDefaultSharedPreferences(ctx)
                .edit()
                .putBoolean(PREF_ENABLED, true)
                .apply()

            if (isPermissionGranted(ctx)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    requestRebind(
                        ComponentName(ctx, NotificationService::class.java)
                    )
            } else {
                requestPermission(ctx)
            }
        }

        fun disableService(ctx: Context) {
            // TODO: Usable of SharedPref
            PreferenceManager.getDefaultSharedPreferences(ctx)
                .edit()
                .putBoolean(PREF_ENABLED, false)
                .apply()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                instance?.requestUnbind()
            else if (isPermissionGranted(ctx))
                requestPermissionDisable(ctx)
        }

        fun isServiceEnabled(ctx: Context) =
            PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(PREF_ENABLED, true)

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

