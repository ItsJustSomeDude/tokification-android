package net.itsjustsomedude.tokens.orders

data class BoostOrderItem(
    val playerName: String,
    val playerDiscordName: String,
    val playerDiscordId: String,
    val tokenRequest: Int = 6,
    val tokensSent: Boolean,
    val boosted: Boolean,
    val sos: Boolean,
    val prefix: String = ""
)
