package net.itsjustsomedude.tokens.reports

import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.ExpandedCoop
import net.itsjustsomedude.tokens.orders.BoostOrderItem
import net.itsjustsomedude.tokens.orders.LuckBoostOrder

class BoostOrderReport : Report() {
    override fun generate(data: ExpandedCoop): String {
        val order: List<BoostOrderItem> = when (data.boostOrder) {
            Coop.BOOST_ORDER_LUCK -> LuckBoostOrder().arrange(data)

            else -> LuckBoostOrder().arrange(data)
        }

        println(order)

        val boosted = order.filter { it.tokensSent }
        val remaining = order.filterNot { it.tokensSent }

        return listOfNotNull(
            "Next Up:",
            if (remaining.isEmpty())
                "None! \uD83C\uDF89"
            else
                remaining.joinToString("\n") { it.asString() },
            "",
            "Boosted:",
            if (boosted.isEmpty())
                "None (yet)"
            else
                boosted.joinToString("\n") { it.asString() },
        ).joinToString("\n")
    }

    private fun BoostOrderItem.asString() =
        listOfNotNull(
            prefix,
            "-",
            if (sos) "\uD83C\uDD98" else null,
            playerName,
            if (tokenRequest != 6) "($tokenRequest)" else null
        ).joinToString(" ")
}