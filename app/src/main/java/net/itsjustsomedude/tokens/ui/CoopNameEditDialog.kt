package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import net.itsjustsomedude.tokens.R
import net.itsjustsomedude.tokens.models.CoopNameEditViewModel

@Composable
fun CoopNameEditDialog(
	initialCoop: String,
	initialKevId: String,
	dialogKey: String = "",
	onDismiss: () -> Unit,
	onConfirm: (coop: String, kevId: String) -> Unit,
	model: CoopNameEditViewModel = CoopNameEditViewModel.provide(
		dialogKey,
		initialCoop,
		initialKevId
	)
) {
	var coop by model.coop
	var kevId by model.kevId

	val clipboardAvailable by model.clipboardAvailable
	val clipboardValid by model.clipboardValid

	AlertDialog(
		onDismissRequest = {
			onDismiss()
		},
		title = {
			Row(
				Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.Top
			) {
				Text("Edit Coop")
				if (clipboardAvailable)
					IconButton(
						modifier = Modifier.size(32.dp),
						onClick = { model.readClipboard() },
						enabled = clipboardValid
					) {
						Icon(
							painter = painterResource(R.drawable.content_paste),
							contentDescription = "Paste"
						)
					}
			}
		},

		text = {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.verticalScroll(rememberScrollState()),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				if (!clipboardValid)
					Text(
						modifier = Modifier.align(Alignment.End),
						text = "Couldn't find coop id in clipboard.",
						color = MaterialTheme.colorScheme.error
					)

				Text("Events are stored by Name and KevID. Changing these will make all existing recorded events disappear.")

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

fun valueSetter(input: String): String? {
	val a = input
		.lowercase()
		.replace(" ", "-")
		.replace("_", "-")
	return if (a.matches(Regex("^[a-z0-9\\-]*\$")))
		a
	else
		null
}
