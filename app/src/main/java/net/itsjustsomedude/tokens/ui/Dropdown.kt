package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(
    label: @Composable () -> Unit,
    initialText: String,
    items: @Composable() (ColumnScope.() -> Unit),
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(initialText) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
        expanded = !expanded
    }) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = selected,
            onValueChange = {},
            label = { label() },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items()

//            items.forEach { item ->
//                DropdownMenuItem(
//                    text = { Text(text = item) },
//                    onClick = {
//                        customPlayerVisible = false
//                        selected = item
//                        playerMenuExpanded = false
//                    },
//                )
//            }
//
//            DropdownMenuItem(
//                text = { Text(text = "Add a player", fontStyle = FontStyle.Italic) },
//                onClick = {
//                    selectedPlayer = "Add a player"
//                    playerMenuExpanded = false
//                    customPlayerVisible = true
//                },
//            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StringDropdown(
    label: @Composable () -> Unit,
    items: List<String>,
    onSelect: (String) -> Unit
) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StringDropdownWithCustom(
    label: @Composable () -> Unit,
    items: List<String>,

    ) {
}