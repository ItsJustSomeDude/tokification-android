package net.itsjustsomedude.tokens

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.db.EventRepository
import net.itsjustsomedude.tokens.reports.BoostOrderReport
import net.itsjustsomedude.tokens.reports.SelfReport
import java.util.Random

class NotificationHelper(
    private val ctx: Context,
    private val coopRepo: CoopRepository,
    private val eventRepo: EventRepository
) {
    init {
        createChannels(ctx)
    }

    private fun createChannels(ctx: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val actionChannel = NotificationChannel(
            ACTION_CHANNEL,
            ctx.getString(R.string.action_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = ctx.getString(R.string.action_channel_desc)
            enableVibration(false)
            setSound(null, null)
        }

        val fakeChannel = NotificationChannel(
            FAKE_CHANNEL,
            ctx.getString(R.string.fake_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = ctx.getString(R.string.fake_channel_desc)
            enableVibration(false)
            setSound(null, null)
        }

        ctx.getSystemService(
            NotificationManager::class.java
        ).apply {
            createNotificationChannel(actionChannel)
            createNotificationChannel(fakeChannel)
        }
    }

    fun sendFake(
        player: String,
        coop: String,
        kevId: String,
        isCR: Boolean
    ) {
        val rand = Random().nextInt(20)
        val textContent = if (isCR)
            "$player ($coop) has hatched $rand chickens for you!"
        else if (rand == 1)
            "$player ($coop) has sent you a Boost Token!"
        else
            "$player ($coop) has sent you a gift of $rand Boost Tokens!"

        val note = NotificationCompat.Builder(ctx, FAKE_CHANNEL)
            .setSmallIcon(R.drawable.offline_bolt)
            .setContentTitle(if (isCR) "\uD83D\uDC23 Gift Received" else "\uD83D\uDCE6 Gift Received")
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup(kevId)
            .build()

        sendNotification(Random().nextInt(), note)

        // TODO: Make this show the most recent note, like EI does,
        val summary = NotificationCompat.Builder(ctx, FAKE_CHANNEL)
            .setContentTitle("New messages")
            .setContentText("This holds all the fake Egg Inc Notifications.")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "If you summoned a Debug Notification and then dismissed it, this will stick around. It's safe to dismiss."
                )
            )
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.offline_bolt)
            .setGroup(kevId)
            .setGroupSummary(true)
            .build()

        sendNotification(Random().nextInt(), summary)
    }

    // Normal Click always opens menu.
    // First button always opens Send
    // Second is Sink 1 if Normal Mode
    // Second is Copy Report if Sink Mode
    // Third is Refresh if Normal mode.

    fun sendSinkActions(coopId: Long, title: String, body: String = "Click to open menu.") {
        val copyReportIntent = PendingIntent.getBroadcast(
            ctx,
            3,
            NotificationActions.copyReportIntent(ctx, coopId).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val note = createBaseActions(coopId)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .addAction(
                R.drawable.copy,
                "Copy Report",
                copyReportIntent
            ).build()

        sendNotification(coopId.toInt(), note)
    }

    fun sendNormalActions(coopId: Long, title: String, body: String) {
        val sink1Intent = PendingIntent.getBroadcast(
            ctx,
            4,
            NotificationActions.sinkTokenIntent(ctx, coopId),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val note = createBaseActions(coopId)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .addAction(
                R.drawable.send,
                "Send 1 Token",
                sink1Intent
            ).build()

        sendNotification(coopId.toInt(), note)
    }

    suspend fun sendActions(coopId: Long) {
        val coop = coopRepo.getCoop(coopId) ?: return
        val events = eventRepo.listEvents(coop.name, coop.contract)

        sendActions(coop, events)
    }

    fun sendActions(coop: Coop, events: List<Event>) {

        // Don't ever post Actions if the Name or KevID is missing.
        if (coop.name.isBlank() || coop.contract.isBlank())
            return

        if (coop.sinkMode) {
            val report = BoostOrderReport().generate(coop, events)

            sendSinkActions(coop.id, coop.name, report)
        } else {
            val report = SelfReport().generate(coop, events)

            sendNormalActions(coop.id, coop.name, report)
        }
    }

    private fun createBaseActions(coopId: Long): NotificationCompat.Builder {
        val openMenuIntent = PendingIntent.getActivity(
            ctx,
            1,
            Intent(ctx, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val createEventIntent = PendingIntent.getActivity(
            ctx,
            2,
            CreateEventActivity.createIntent(ctx, coopId).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val refreshIntent = PendingIntent.getBroadcast(
            ctx,
            5,
            NotificationActions.refreshNotificationIntent(ctx, coopId),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // TODO: Add a way to make the notification un-dismissible.
        return NotificationCompat.Builder(ctx, ACTION_CHANNEL)
            .setSmallIcon(R.drawable.offline_bolt)
            .setAutoCancel(false)
            .setContentIntent(openMenuIntent)
            .addAction(
                R.drawable.send,
                "Send Tokens",
                createEventIntent
            ).addAction(
                R.drawable.refresh,
                "Refresh",
                refreshIntent
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVibrate(null)
            .setSound(null)
    }

    private fun sendNotification(id: Int, note: Notification) {
        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ctx !is Activity || !ActivityCompat.shouldShowRequestPermissionRationale(
                    ctx,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                Toast.makeText(
                    ctx,
                    "Please grant notification permissions in settings/App Info!",
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val toRequest = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                ActivityCompat.requestPermissions(ctx, toRequest, 1)
            }

            return
        }

        NotificationManagerCompat.from(ctx).notify(id, note)
    }

    companion object {
        private const val TAG = "Notifications"

        private const val ACTION_CHANNEL = "Actions"
        private const val FAKE_CHANNEL = "Fake"

        fun requestPermission(ctx: Activity) {
            if (ActivityCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val toRequest = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                    ActivityCompat.requestPermissions(ctx, toRequest, 1)
                }
            }
        }
    }
}
