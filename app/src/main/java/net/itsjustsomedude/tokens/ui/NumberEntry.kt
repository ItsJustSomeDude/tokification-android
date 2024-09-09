package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun NumberEntry(
    value: Int,
    onChange: (Int) -> Unit
) {
    var valueState by remember { mutableIntStateOf(value) }
    val countPattern = remember { Regex("^\\d{1,4}\$") }

    Row {
        IconButton(onClick = {
            if (valueState != 0) {
                valueState--
            }
        }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Subtract 1"
            )
        }

        TextField(
            value = valueState.toString(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                if (it.matches(countPattern)) {
                    valueState = it.toInt()
                } else if (it.isEmpty()) {
                    valueState = 0
                }
            },
            modifier = Modifier.width(64.dp)
        )

        IconButton(onClick = {
            if (valueState != 9999) {
                valueState++
            }
        }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Add 1"
            )
        }
    }
}
