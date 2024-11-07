package net.itsjustsomedude.tokens.reports

import net.itsjustsomedude.tokens.BuildConfig
import net.itsjustsomedude.tokens.db.ExpandedCoop

class SinkReport : Report() {
	override fun generate(data: ExpandedCoop): String = with(data) {
		val startInfoLine =
			if (isStartEstimated) ":warning: No start time set, assuming start of current hour."
			else "<:contract_scroll_white:1291832469179338796> Start Time: <t:$startEpoch> (<t:$startEpoch:R>)"

		val endInfoLine =
			if (isEndEstimated) ":warning: No end time set, assuming 12 hours from start time."
			else ":alarm_clock: End Time: <t:$endEpoch> (<t:$endEpoch:R>)"

		val futureTvalTable = if (isEnded) ":tada: Contract Complete!"
		else listOfNotNull(
			"__Running Token Value__",
			"<:icon_token:653018008670961665> Now: `$tvalNow`",
			if (tvalNow > 0.03) "In 30 minutes: `$tval30Mins`" else null,
			if (tval30Mins > 0.03) "In 60 minutes: `$tval60Mins`" else null,
		).joinToString("\n")

		val generationInfoLine = "Report Generated at <t:$nowEpoch> (<t:$nowEpoch:R>)"

		// Keyed by Player Name, value is table row.
		val tvalTable = mutableMapOf<String, String>()

		val sinkTableFormat = "%-12.12s|%9.3f|%4d|%8.3f|%4d|%9.3f"

		for (person in players) {
			val sent = tvalSent[person] ?: 0.0
			val rec = tvalRec[person] ?: 0.0
			val delta = sent - rec

			val output = sinkTableFormat.format(
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

		val selfTableRow = sinkTableFormat.format(
			"Sink",
			data.selfTvalDelta,
			data.selfTokensSent,
			data.selfTvalSent,
			data.selfTokensReceived,
			data.selfTvalReceived
		)

		return arrayOf(
			"# __Tokification__",
			"-# _${BuildConfig.VERSION_NAME}_",
			"",
			generationInfoLine,
			"_This message can be manually updated by asking the sink politely._",
			"",
			"__Contract Info__",
			startInfoLine,
			endInfoLine,
			"_Token values are only accurate once the end time is accurate._",
			"",
			futureTvalTable,
			"",
			"__Player's Current TVals__ (as seen by the :people_hugging: sink)",
			"```",
			"Player      |   Δ TVal| +TS|  +TSVal| -TR|   -TRVal",
			"------------+---------+----+--------+----+---------",
			tvalTable.values.joinToString("\n"),
			selfTableRow,
			"```",
			"_This is not a Wonky command, but an app written by ItsJustSomeDude. Stay tuned for further updates!_",
		).joinToString("\n")
	}
}
