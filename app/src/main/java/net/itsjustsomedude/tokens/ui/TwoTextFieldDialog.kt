package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TwoTextFieldDialog(
    title: String,
    text: String? = null,
    field1Label: String = "",
    field2Label: String = "",
    field1Initial: String = "",
    field2Initial: String = "",
    confirmLabel: String = "OK",
    cancelLabel: String = "Cancel",
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    valueSetter: (String) -> String? = { it }
) {
    var field1 by remember { mutableStateOf(field1Initial) }
    var field2 by remember { mutableStateOf(field2Initial) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (text != null) {
                    Text(text)
                }
                OutlinedTextField(
                    value = field1,
                    singleLine = true,
                    onValueChange = {
                        val newVal = valueSetter(it)
                        if (newVal != null)
                            field1 = newVal
                    },
                    label = { Text(field1Label) }
                )
                OutlinedTextField(
                    value = field2,
                    singleLine = true,
                    onValueChange = {
                        val newVal = valueSetter(it)
                        if (newVal != null)
                            field2 = newVal
                    },
                    label = { Text(field2Label) },
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(field1, field2) }) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelLabel)
            }
        }
    )
}

@Preview
@Composable
fun PreviewTwoTextFieldsDialog() {
    MaterialTheme {
        TwoTextFieldDialog(
            onDismiss = {},
            onConfirm = { _, _ ->
            },
            title = "Demo",
            text = "Yeah!\nCool!",
            field1Initial = "Init?"
        )
    }
}