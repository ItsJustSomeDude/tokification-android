package net.itsjustsomedude.tokens.reports

class SinkReport : Report() {
    override fun generate(data: ReportData): String {
        return arrayOf(
            "# __Tokification__ (Android Alpha :eyes:)",
            "",
            data.generationInfoLine,
            "_This message will be manually updated every 15 to 45 minutes, depending on how busy I am._",
            "",
            "__Contract Info__",
            data.startInfoLine,
            data.endInfoLine,
            "_Note that all token values are only accurate once the end time is accurate._",
            "",
            data.futureTvalTable,
            "",
            "__Player's Current TVals__ (as seen by the :people_hugging: sink)",
            "```",
            "Player      |   Δ TVal| +TS|  +TSVal| -TR|   -TRVal",
            "------------+---------+----+--------+----+---------",
            data.tvalTable.values.joinToString("\n"),
            "```",
            "_This is not a wonky command, but an app written by ItsJustSomeDude. Stay tuned for further updates!_",
        ).joinToString("\n")
    }
}
