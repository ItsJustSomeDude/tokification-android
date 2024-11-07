package net.itsjustsomedude.tokens.reports

import net.itsjustsomedude.tokens.db.ExpandedCoop
import net.itsjustsomedude.tokens.round

class SelfReport : Report() {
	override fun generate(data: ExpandedCoop): String {
		var est = ""
		if (data.isStartEstimated) est += "(Unknown Start)"
		if (data.isEndEstimated) est += "(Assuming 12 hour duration)"

		val tvSent = data.selfTvalSent
		val tvRec = data.selfTvalReceived
		val tSent = data.selfTokensSent
		val tRec = data.selfTokensReceived

		return arrayOf(
			String.format("Your ΔTVal: %s %s", round(tvSent - tvRec, 5), est),
			String.format(
				"TVal Now: %s",
				if (data.isEnded) "Contract Complete!" else round(data.tvalNow, 5)
			),
			String.format("Sent TVal: %s (%s tokens)", round(tvSent, 5), tSent),
			String.format("Received TVal: -%s (%s tokens)", round(tvRec, 4), tRec)
		).joinToString("\n")
	}
}