package net.itsjustsomedude.tokens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.db.EventRepository
import net.itsjustsomedude.tokens.reports.SinkReport
import org.koin.mp.KoinPlatform.getKoin

private const val TAG = "NotificationActions"

class NotificationActions : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        val scope = CoroutineScope(Dispatchers.IO)
        val notificationHelper: NotificationHelper = getKoin().get()

        val coopId = intent.getLongExtra(PARAM_COOP_ID, 0)
        if (coopId == 0L) {
            Log.e(TAG, "No CoopID passed in bundle.")
            return
        }

        when (intent.action) {
            ACTION_COPY_REPORT -> scope.launch {
                val report = SinkReport().generate(coopId)

                ClipboardHelper(ctx).copyText(report)
            }

            ACTION_REFRESH_NOTIFICATION -> scope.launch {
                notificationHelper.sendActions(coopId)
            }

            ACTION_SINK_TOKEN -> scope.launch {
                val coopRepo: CoopRepository = getKoin().get()
                val eventRepo: EventRepository = getKoin().get()

                val coop = coopRepo.getCoop(coopId) ?: return@launch

                eventRepo.insert(
                    eventRepo.newEvent(
                        coop = coop,
                        person = "Sink",
                        count = 1
                    )
                )

                notificationHelper.sendActions(coopId)
            }
        }
    }

    companion object {
        private const val ACTION_COPY_REPORT = "net.itsjustsomedude.tokens.COPY_REPORT"
        private const val ACTION_REFRESH_NOTIFICATION =
            "net.itsjustsomedude.tokens.REFRESH_NOTIFICATION"
        private const val ACTION_SINK_TOKEN = "net.itsjustsomedude.tokens.SINK_TOKEN"

        private const val PARAM_COOP_ID = "CoopId"

        fun copyReportIntent(ctx: Context, coopId: Long) =
            Intent(ctx, NotificationActions::class.java).apply {
                action = ACTION_COPY_REPORT
                putExtra(PARAM_COOP_ID, coopId)
            }

        fun refreshNotificationIntent(ctx: Context, coopId: Long) =
            Intent(ctx, NotificationActions::class.java).apply {
                action = ACTION_REFRESH_NOTIFICATION
                putExtra(PARAM_COOP_ID, coopId)
            }

        fun sinkTokenIntent(ctx: Context, coopId: Long) =
            Intent(ctx, NotificationActions::class.java).apply {
                action = ACTION_SINK_TOKEN
                putExtra(PARAM_COOP_ID, coopId)
            }
    }
}