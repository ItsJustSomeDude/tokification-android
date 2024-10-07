package net.itsjustsomedude.tokens.reports

class LuckBoostReport : Report() {
    override fun generate(data: ReportData): String {
        val sortedSentMap = data.tvalSent.toList()
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
            val numTokens = data.tokensSent[entry.key] ?: 0
            val row = "$numTokens - ${entry.key}"

            if (boostedPlayers.contains(entry.key))
                outputBoosted.add(row)
            else
                outputRemaining.add(row)
        }

        // Report:
        // Remaining players, sorted by Tokens Sent
        // Players that have not sent OR received yet
        // Players that have already boosted

        return listOfNotNull(
            "Next Up:",
            if (outputRemaining.isEmpty())
                "None! \uD83C\uDF89"
            else
                outputRemaining.joinToString("\n"),
            "",
            "Boosted:",
            if (outputBoosted.isEmpty())
                "None (yet)"
            else
                outputBoosted.joinToString("\n"),
        ).joinToString("\n")
    }
}