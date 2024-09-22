package net.itsjustsomedude.tokens

import android.app.Notification
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
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.NotificationReader.NotificationService
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.db.EventRepository
import org.koin.android.ext.android.getKoin
import java.util.Calendar
import java.util.regex.Pattern

object NotificationReader {
    const val PREF_ENABLED: String = "service_control_enable_service"
    const val PREF_DISMISS: String = "auto_dismiss"

    private const val TAG = "NotificationReader"

    private val personCoopRegex =
        Pattern.compile("^(.+) \\((.+)\\) has (?:sent you|hatched).+?$")
    private val tokenCountRegex = Pattern.compile("(?<=has sent you a gift of )([0-9]+)")

    // TODO: Set in the root_preferences file.
    private const val dismissDelay = 10 * 1000

    @JvmStatic
    fun processAllNotifications() {
        NotificationService.instance?.processAllNotifications()
    }

    @JvmStatic
    fun isServiceEnabled(ctx: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(PREF_ENABLED, true)
    }

    @JvmStatic
    val isServiceRunning: Boolean
        get() {
            val i = NotificationService.instance
            return i != null
        }

    @JvmStatic
    fun setServiceEnabled(ctx: Context, enable: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(ctx)
            .edit()
            .putBoolean(PREF_ENABLED, enable)
            .apply()

        val service = NotificationService.instance
        if (enable && service == null) {
            // Enable Service, it's not running.
            val cn = ComponentName(ctx, NotificationService::class.java)
            val flat =
                Settings.Secure.getString(ctx.contentResolver, "enabled_notification_listeners")

            if (flat != null && flat.contains(cn.flattenToString())) {
                Log.i(TAG, "Asking it to start via requestRebind.")
                // TODO: Offer enable/disable from settings if android below N.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    NotificationListenerService.requestRebind(
                        ComponentName(
                            ctx,
                            NotificationService::class.java
                        )
                    )
                }
            } else {
                Toast.makeText(
                    ctx,
                    ctx.getString(R.string.enable_service_toast),
                    Toast.LENGTH_LONG
                ).show()

                ctx.startActivity(
                    Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                )
            }
        } else if (!enable && service != null) {
            service.stop()
        }
    }

    class NotificationService : NotificationListenerService() {
        private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        private var dismissHandler: Handler? = null

        override fun onListenerConnected() {
            instance = this

            if (isServiceEnabled(this)) {
                dismissHandler = Handler(Looper.getMainLooper())
            } else {
                stop()
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

        fun stop() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                requestUnbind()
            } else {
                // TODO: Open an old emulator and see if this works.
                stopSelf()
            }
        }

        private fun destroy() {
            serviceScope.cancel()

            instance = null

            if (dismissHandler != null) {
                dismissHandler!!.removeCallbacksAndMessages(null)
                dismissHandler = null
            }
        }

        fun processAllNotifications() {
            if (!isServiceEnabled(this))
                return

            val repo: EventRepository = getKoin().get()
            for (sbn in activeNotifications) {
                val sbnData = extractData(sbn)

                if (sbnData.shouldProcess)
                    serviceScope.launch {
                        processNotification(sbnData, repo)
                    }
            }
        }

        override fun onNotificationPosted(sbn: StatusBarNotification) {
            val note = extractData(sbn)

            if (!isServiceEnabled(this) || !note.shouldProcess)
                return

            serviceScope.launch {
                processNotification(note, getKoin().get())
            }
        }

        private suspend fun processNotification(
            note: ShortNotification,
            repository: EventRepository
        ) {
            val matches = personCoopRegex.matcher(note.text)
            if (!matches.lookingAt() || matches.groupCount() < 2) {
                Log.e(TAG, "Person/Coop Regex didn't match for notification content:")
                Log.e(TAG, note.text)
                return
            }

            val person = matches.group(1)!!
            val coopName = matches.group(2)!!

            if (repository.exists(coopName, note.group, note.id)) {
                Log.i(TAG, "Skipping notification that has already been processed.")
                scheduleRemoval(note.key)
                return
            }

            if (note.title.contains("🐣")) {
                Log.i(TAG, "Processing CR")
                repository.insert(
                    Event(
                        coop = coopName,
                        kevId = note.group,
                        person = person,
                        count = 0,      // Count of 0 indicates CR.
                        time = note.time,
                        direction = Event.DIRECTION_SENT,
                        notification = note.id
                    )
                )
                scheduleRemoval(note.key)
                return
            }

            val amount: Int
            if (note.text.contains("has sent you a Boost Token")) amount = 1
            else {
                try {
                    val countMatch = tokenCountRegex.matcher(note.text)

                    if (!countMatch.find() || countMatch.group(1) == null) {
                        Log.e(
                            TAG,
                            "Count Regex didn't match for notification content: ${note.text}"
                        )
                        return
                    }


                    amount = countMatch.group(1)!!.toInt()
                } catch (err: NumberFormatException) {
                    Log.e(TAG, "Bad number in note: ${note.text}")
                    return
                }
            }

            repository.insert(
                Event(
                    coop = coopName,
                    kevId = note.group,
                    person = person,
                    count = amount,
                    time = note.time,
                    direction = Event.DIRECTION_SENT,
                    notification = note.id
                )
            )

            Log.i(TAG, "Added event from note: ${note.text}")

            scheduleRemoval(note.key)
        }

        private fun scheduleRemoval(key: String) {
            if (shouldDismiss())
                dismissHandler!!.postDelayed(
                    { cancelNotification(key) },
                    dismissDelay.toLong()
                )
        }

        // TODO: Make less calls to shared prefs, cache this.
        private fun shouldDismiss(): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_DISMISS, false)
        }

        companion object {
            var instance: NotificationService? = null
                private set
        }
    }
}

fun extractData(sbn: StatusBarNotification): ShortNotification {
    val innerNote = sbn.notification

    val bigText = innerNote.extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
    val extraText = innerNote.extras.getCharSequence(Notification.EXTRA_TEXT)

    val text = bigText?.toString() ?: (extraText?.toString() ?: "")

    val t = innerNote.extras.getCharSequence(Notification.EXTRA_TITLE)
    val title = t?.toString() ?: ""

    val time = Calendar.getInstance()
    time.timeInMillis = innerNote.`when`

    val user = sbn.user

    println("Attempt at user id: ${user.hashCode()}")

    return ShortNotification(
        id = sbn.id,
        key = sbn.key,
        group = innerNote.group ?: "",
        packageName = sbn.packageName,
        flags = innerNote.flags,
        text = text,
        title = title,
        time = time,
//        user = user
    )
}

private val ALLOWED_PACKAGES = listOf(
    "com.auxbrain.egginc",
    "net.itsjustsomedude.tokens",
    "net.itsjustsomedude.tokens.debug"
)

data class ShortNotification(
    val id: Int,
    val key: String,
    val flags: Int,
    val packageName: String,
    val text: String,
    val title: String,
    val group: String,
    val time: Calendar,
//    val user: Int
) {
    val shouldProcess: Boolean
        get() {
            if (!ALLOWED_PACKAGES.contains(packageName)) return false

            if ((flags and Notification.FLAG_GROUP_SUMMARY) != 0) return false

            if (!title.contains("Gift Received")) return false

            return true
        }
}