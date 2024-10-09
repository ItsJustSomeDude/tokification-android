package net.itsjustsomedude.tokens.reports

import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.round
import net.itsjustsomedude.tokens.tval

class DetailedReport : Report() {
    override fun generate(data: ReportData): String {
        if (data.isStartEstimated || data.isEndEstimated)
            return "Start and End are required for detailed report."

        val header = "Elapse|# |D|Prior|Diff"
        val rowFormat = "`%6d|%2d|%1s|%5.2f|%4.2f|`<t:%s:f>"

        val rows = mutableMapOf<String, MutableList<String>>()
        val cums = mutableMapOf<String, Double>()

        for (ev in data.events) {
            val t = ev.time.timeInMillis / 1000L
            val elapsedSeconds = t - data.startEpoch

            val tv: Double
            val direction: String
            if (ev.direction == Event.DIRECTION_SENT) {
                direction = "→"
                tv = tval(data.startEpoch, data.endEpoch, t, ev.count)
            } else {
                direction = "←"
                tv = -1 * tval(data.startEpoch, data.endEpoch, t, ev.count)
            }

            val row = rowFormat.format(
                elapsedSeconds,
                ev.count,
                direction,
                cums[ev.person] ?: 0.0,
//                sign,
                tv,
                t
            )

            if (!rows.containsKey(ev.person)) {
                rows[ev.person] = mutableListOf()
            }

            rows[ev.person]!!.add(row)
            cums[ev.person] = (cums[ev.person] ?: 0.0) + tv
        }

        val table = mutableListOf(
            "# __Tokification Detailed Report__",
            "Key:",
            "`Time (timeElapsed): count ↔ direction: runningDelta ±change`",
            ""
        )

        for (person in rows.keys) {
            table.addAll(
                listOf(
                    "## __${person}__",
                    "`$header`",
                    rows[person]!!.joinToString("\n"),
                    "Final TVal: `${round(cums[person]!!, 2)}`",
                    ""
                )
            )
        }

        return table.joinToString("\n")
    }
}
