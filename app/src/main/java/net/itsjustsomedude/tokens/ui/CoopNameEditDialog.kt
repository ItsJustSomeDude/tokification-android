package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import net.itsjustsomedude.tokens.R
import net.itsjustsomedude.tokens.models.CoopNameEditViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CoopNameEditDialog(
    initialCoop: String,
    initialKevId: String,
    onDismiss: () -> Unit,
    onConfirm: (coop: String, kevId: String) -> Unit,
    model: CoopNameEditViewModel = koinViewModel { parametersOf(initialCoop, initialKevId) }
) {
    val regex = remember { Regex("^[a-z0-9\\-]*\$") }

    var coop by model.coop
    var kevId by model.kevId

    val clipboardValid by model.isClipboardValid

    val valueSetter: (String) -> String? = {
        val a = it
            .lowercase()
            .replace(" ", "-")
            .replace("_", "-")
        if (a.matches(regex)) {
            a
        } else null
    }

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = { Text("Edit Coop") },

        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Something about editing this stuff, yeah.")

                OutlinedTextField(
                    value = coop,
                    singleLine = true,
                    onValueChange = {
                        valueSetter(it)?.let { newVal ->
                            coop = newVal
                        }
                    },
                    label = { Text("Coop Name") }
                )

                OutlinedTextField(
                    value = kevId,
                    singleLine = true,
                    onValueChange = {
                        valueSetter(it)?.let { newVal ->
                            kevId = newVal
                        }
                    },
                    label = { Text("KevID") },
                )

                if (clipboardValid)
                    Button(
                        colors = ButtonDefaults.elevatedButtonColors(),
                        elevation = ButtonDefaults.filledTonalButtonElevation(),
                        onClick = {
                            // TODO: This spams toasts... must fix.
                            model.readClipboard()
                            onConfirm(coop, kevId)
                        }) {
                        Icon(
                            painter = painterResource(R.drawable.content_paste),
                            contentDescription = "Edit Coop"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            fontFamily = FontFamily.Monospace,
                            text = "${model.clipboardCoop.value}/${model.clipboardKevId.value}"
                        )
                    }

            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(coop, kevId) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}