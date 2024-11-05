package net.itsjustsomedude.tokens.db

import net.itsjustsomedude.tokens.db.Coop.Companion.BOOST_ORDER_UNKNOWN
import net.itsjustsomedude.tokens.roundedTval
import net.itsjustsomedude.tokens.tval
import java.util.Calendar
import java.util.Locale

data class ExpandedCoop(
    val id: Long = 0,

    val name: String = "",
    val contract: String = "",
    val startTime: Calendar? = null,
    val endTime: Calendar? = null,
    val sinkMode: Boolean = false,
    val boostOrder: Int = BOOST_ORDER_UNKNOWN,

    val players: List<String> = emptyList(),
    val sink: String = "",

    /** Used to lock a players into certain boost order positions. */
    val playerPositionOverrides: Map<String, Int> = emptyMap(),

    /** Used to have certain players following a different boost order than the default. */
    val playerOrderOverrides: Map<String, Int> = emptyMap(),

    /** Overrides the number of tokens each player wants. */
    val playerTokenAmounts: Map<String, Int> = emptyMap(),

    val events: List<Event> = emptyList()
) {
    val isStartEstimated = startTime == null
    val isEndEstimated = endTime == null

    val nowEpoch = Calendar.getInstance().timeInMillis / 1000L

    val startEpoch = startTime?.let {
        it.timeInMillis / 1000L
    } ?: (nowEpoch % 3600)

    val endEpoch = endTime?.let {
        it.timeInMillis / 1000L
    } ?: (startEpoch + 12 * 60 * 60)

    val isEnded = nowEpoch > endEpoch

    val startInfoLine = if (isStartEstimated)
        ":warning: No start time set, assuming start of current hour."
    else
        "<:contract_scroll_white:1291832469179338796> Start Time: <t:$startEpoch> (<t:$startEpoch:R>)"

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

    // TODO: Move the table code into the actual sink report.
    // It's a mess where it is.
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

        tvalDelta = players.associateWith { player ->
            (tvalSent[player] ?: 0.0) - (tvalRec[player] ?: 0.0)
        }

        this.selfTokensSent = selfTokensSent
        this.selfTokensReceived = selfTokensReceived
        this.selfTvalSent = selfTvalSent
        this.selfTvalReceived = selfTvalReceived

        // Keyed by Player Name, value is table row.
        val tvalTable = mutableMapOf<String, String>()

        for (person in players) {
            val sent = tvalSent[person] ?: 0.0
            val rec = tvalRec[person] ?: 0.0
            val delta = sent - rec

            val output = String.format(
                Locale.US,
                SINK_TABLE_FORMAT,
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
        tvalTableString = tvalTable.values.joinToString("\n")
    }

    val coop
        get() = Coop(
            id,
            name,
            contract,
            startTime,
            endTime,
            sinkMode,
            boostOrder,
            players,
            sink,
            playerPositionOverrides,
            playerOrderOverrides,
            playerTokenAmounts
        )

    companion object {
        const val SINK_TABLE_FORMAT = "%-12.12s|%9.3f|%4d|%8.3f|%4d|%9.3f"
    }
}

fun Coop.expand(events: List<Event>) = ExpandedCoop(
    id,
    name,
    contract,
    startTime,
    endTime,
    sinkMode,
    boostOrder,
    players,
    sink,
    playerPositionOverrides,
    playerOrderOverrides,
    playerTokenAmounts,
    events
)