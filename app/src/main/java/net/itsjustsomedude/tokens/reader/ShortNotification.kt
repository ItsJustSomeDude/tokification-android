package net.itsjustsomedude.tokens.reader

import android.app.Notification
import android.service.notification.StatusBarNotification
import java.util.Calendar

class ShortNotification(
    sbn: StatusBarNotification
) {
    private val innerNote: Notification? = sbn.notification

    private val bigText = innerNote?.extras?.getCharSequence(Notification.EXTRA_BIG_TEXT)
    private val extraText = innerNote?.extras?.getCharSequence(Notification.EXTRA_TEXT)

    val id = sbn.id
    val key: String = sbn.key
    val group = innerNote?.group ?: ""

    val title = innerNote?.extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
    val text = bigText?.toString() ?: (extraText?.toString() ?: "")
    val time: Calendar = Calendar.getInstance()
    val user = sbn.user.hashCode()

    val packageName = innerNote?.group ?: ""
    val flags = innerNote?.flags ?: 0

    init {
        println("Attempt at user id: $user")

        innerNote?.let {
            time.timeInMillis = it.`when`
        }
    }
}