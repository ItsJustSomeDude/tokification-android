package net.itsjustsomedude.tokens.ui.components

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Density

fun Modifier.skeletonColors(): Modifier {
    return this.background(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Gray,
                Color.LightGray
            )
        )
    )
}

fun Modifier.debugRuler(density: Density, name: String): Modifier {
    return this.onSizeChanged {
        println("$name Width: ${with(density) { it.width.toDp() }}, Height: ${with(density) { it.height.toDp() }}")
    }
}