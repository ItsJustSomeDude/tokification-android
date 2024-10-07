package net.itsjustsomedude.tokens.reports

import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.roundedTval
import net.itsjustsomedude.tokens.tval
import java.util.Calendar
import java.util.Locale

data class ReportData(
    val coop: Coop,
    val events: List<Event>
) {
    val isStartEstimated = coop.startTime == null

    val isEndEstimated = coop.endTime == null

    val nowEpoch = Calendar.getInstance().timeInMillis / 1000L

    val startEpoch = coop.startTime?.let {
        it.timeInMillis / 1000L
    } ?: (nowEpoch % 3600)

    val endEpoch = coop.endTime?.let {
        it.timeInMillis / 1000L
    } ?: (startEpoch + 12 * 60 * 60)

    val isEnded = (nowEpoch > endEpoch)

    val startInfoLine = if (isStartEstimated)
        ":warning: No start time set, assuming start of current hour."
    else
        "<:contract:589317482901405697> Start Time: <t:$startEpoch> (<t:$startEpoch:R>)"

    val endInfoLine = if (isEndEstimated)
        ":warning: No end time set, assuming 12 hours from start time."
    else
        ":alarm_clock: End Time: <t:$endEpoch> (<t:$endEpoch:R>)"

    val tvalNow = roundedTval(4, startEpoch, endEpoch, nowEpoch, 1)
    val tval30Mins = roundedTval(4, startEpoch, endEpoch, nowEpoch + 30 * 60, 1)
    val tval60Mins = roundedTval(4, startEpoch, endEpoch, nowEpoch + 60 * 60, 1)

    val futureTvalTable =
        if (isEnded)
            ":tada: Contract Complete!"
        else listOfNotNull(
            "__Running Token Value__",
            "<:icon_token:653018008670961665> Now: `$tvalNow`",
            if (tvalNow > 0.03) "In 30 minutes: `$tval30Mins`" else null,
            if (tval30Mins > 0.03) "In 60 minutes: `$tval60Mins`" else null,
        ).joinToString("\n")

    val generationInfoLine = "Report Generated at <t:$nowEpoch> (<t:$nowEpoch:R>)"

    val tokensSent = mutableMapOf<String, Int>()
    val tokensRec = mutableMapOf<String, Int>()
    val tvalSent = mutableMapOf<String, Double>()
    val tvalRec = mutableMapOf<String, Double>()

    val tvalDelta: Map<String, Double>

    val selfTokensSent: Int
    val selfTokensReceived: Int
    val selfTvalSent: Double
    val selfTvalReceived: Double

    val selfTvalDelta: Double

    // Keyed by Player Name, value is table row.
    val tvalTable: Map<String, String>
    val tvalTableString: String

    init {
        var selfTokensSent = 0
        var selfTokensReceived = 0
        var selfTvalSent = 0.0
        var selfTvalReceived = 0.0

        for ((_, _, _, rawTime, count, person, direction) in events) {
            val time: Long = rawTime.timeInMillis / 1000L

            val tv = tval(startEpoch, endEpoch, time, count)
            if (direction == Event.DIRECTION_SENT) {
                tokensSent[person] = (tokensSent[person] ?: 0) + count
                tvalSent[person] = (tvalSent[person] ?: 0.0) + tv

                selfTokensReceived += count
                selfTvalReceived += tv
            } else {
                tokensRec[person] = (tokensRec[person] ?: 0) + count
                tvalRec[person] = (tvalRec[person] ?: 0.0) + tv

                selfTokensSent += count
                selfTvalSent += tv
            }
        }

        selfTvalDelta = selfTvalSent - selfTvalReceived

        tvalDelta = tvalSent.mapValues { (key, value) ->
            value - (tvalRec[key] ?: 0.0)
        }

        this.selfTokensSent = selfTokensSent
        this.selfTokensReceived = selfTokensReceived
        this.selfTvalSent = selfTvalSent
        this.selfTvalReceived = selfTvalReceived

        // Keyed by Player Name, value is table row.
        val tvalTable = mutableMapOf<String, String>()
        val rowFormat = "%-12.12s|%9.3f|%4d|%8.3f|%4d|%9.3f"

        for (person in coop.players) {
            val sent = tvalSent[person] ?: 0.0
            val rec = tvalRec[person] ?: 0.0
            val delta = sent - rec

            val output = String.format(
                Locale.US,
                rowFormat,
                person,
                delta,
                tokensSent[person] ?: 0,
                sent,
                tokensRec[person] ?: 0,

                // This looks odd, but it's to prevent -0.0 from showing up.
                if (rec == 0.0) 0.0 else (rec * -1)
            )
            tvalTable[person] = output
        }

        this.tvalTable = tvalTable
        this.tvalTableString = tvalTable.values.joinToString("\n")
    }
}

//val a = listOf(
//    "isStartEstimate" to startEstimate,
//    "isEndEstimate" to endEstimate,
//    "isEnded" to ended,
//    "nowEpoch" to nowEpoch,
//    "startEpoch" to startEpoch,
//    "endEpoch" to endEpoch,
//    "runningTvalNow" to tvalNow,
//    "runningTval30Mins" to tval30Mins,
//    "runningTval60Mins" to tval60Mins,
//    "reportInfoLine" to "Report Generated at <t:$nowEpoch> (<t:$nowEpoch:R>)",
//    "startInfoLine" to startLine,
//    "endInfoLine" to endLine,
//    "playersTvalTable" to tvalTable,
//    "futureRunningTvalTable" to futureTable
//)
