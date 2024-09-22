package net.itsjustsomedude.tokens.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity
data class Coop(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String = "",
    val contract: String = "",
    val startTime: Calendar? = null,
    val endTime: Calendar? = null,
    val sinkMode: Boolean = false,
    val players: List<String> = emptyList()
)
