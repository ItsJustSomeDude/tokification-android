package net.itsjustsomedude.tokens.reports

class LuckBoostReport : Report() {
    override fun generate(data: ReportData): String {
        val sortedSentMap = data.tokensSent.toList()
            .sortedByDescending { it.second }
            .toMap()

        val allPlayers = mutableListOf<String>()
        allPlayers.addAll(data.coop.players)

        // List of all players that have received tokens.
        val boostedPlayers = mutableListOf<String>()
        boostedPlayers.addAll(data.tokensRec.keys)

        // List of all players that have NOT received tokens.
        val remainingPlayers = mutableListOf<String>()
        remainingPlayers.addAll(allPlayers - boostedPlayers.toSet())

        val outputRemaining = mutableListOf<String>()
        val outputBoosted = mutableListOf<String>()

        for (entry in sortedSentMap) {
            if (boostedPlayers.contains(entry.key))
                outputBoosted.add("${entry.value} - ${entry.key}")
            else
                outputRemaining.add("${entry.value} - ${entry.key}")
        }

        // Report:
        // Remaining players, sorted by Tokens Sent
        // Players that have not sent OR received yet
        // Players that have already boosted

        return listOf(
//            "(Beta) Lucky Boost Order!",
            "Next Up:",
            outputRemaining.joinToString("\n"),
            "",
            "Boosted:",
            outputBoosted.joinToString("\n"),
            "",
            "Beta feature, please report any issues!"
        ).joinToString("\n")
    }
}