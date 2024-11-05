package net.itsjustsomedude.tokens.reports

import net.itsjustsomedude.tokens.BuildConfig
import net.itsjustsomedude.tokens.db.ExpandedCoop
import net.itsjustsomedude.tokens.db.ExpandedCoop.Companion.SINK_TABLE_FORMAT

class SinkReport : Report() {
    override fun generate(data: ExpandedCoop): String {
        return arrayOf(
            "# __Tokification__",
            "-# _${BuildConfig.VERSION_NAME}_",
            "",
            data.generationInfoLine,
            "_This message can be manually updated by asking the sink politely._",
            "",
            "__Contract Info__",
            data.startInfoLine,
            data.endInfoLine,
            "_Token values are only accurate once the end time is accurate._",
            "",
            data.futureTvalTable,
            "",
            "__Player's Current TVals__ (as seen by the :people_hugging: sink)",
            "```",
            "Player      |   Δ TVal| +TS|  +TSVal| -TR|   -TRVal",
            "------------+---------+----+--------+----+---------",
            data.tvalTable.values.joinToString("\n"),
            SINK_TABLE_FORMAT.format(
                "Sink",
                data.selfTvalDelta,
                data.selfTokensSent,
                data.selfTvalSent,
                data.selfTokensReceived,
                data.selfTvalReceived
            ),
            "```",
            "_This is not a Wonky command, but an app written by ItsJustSomeDude. Stay tuned for further updates!_",
        ).joinToString("\n")
    }
}
