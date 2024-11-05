package net.itsjustsomedude.tokens.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NumberEntry(
    modifier: Modifier = Modifier,
    value: Int,
    onChanged: (Int) -> Unit
) {
    var valueState by remember { mutableIntStateOf(value) }
    val countPattern = remember { Regex("^\\d{1,4}\$") }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            if (valueState != 0) {
                onChanged(--valueState)
            }
        }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Subtract 1"
            )
        }

        TextField(
            shape = RoundedCornerShape(4.dp),
            value = valueState.toString(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                if (it.matches(countPattern)) {
                    valueState = it.toInt()
                } else if (it.isEmpty()) {
                    valueState = 0
                }
                onChanged(valueState)
            },
            modifier = Modifier.width(64.dp)
        )

        IconButton(onClick = {
            if (valueState != 9999) {
                onChanged(++valueState)
            }
        }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Add 1"
            )
        }
    }
}

@Composable
fun NumberEntrySkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}, enabled = false) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Subtract 1"
            )
        }

        Box(
            Modifier
                .size(64.dp, 56.dp)
                .clip(RoundedCornerShape(4.dp))
                .skeletonColors()
        )

        IconButton(onClick = {}, enabled = false) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Add 1"
            )
        }
    }
}

@Preview
@Composable
fun PreviewNumberEntry() {
    NumberEntry(value = 12, onChanged = {})
}

@Preview
@Composable
fun PreviewNumberEntrySkeleton() {
    NumberEntrySkeleton()
}

