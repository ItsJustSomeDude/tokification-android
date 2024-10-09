package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun YesNoDialog(
    onYesClick: () -> Unit,
    onNoClick: () -> Unit,
    onDismissRequest: () -> Unit,
    title: String? = null,
    yesButtonText: String = "Yes",
    noButtonText: String = "No",
    content: @Composable ColumnScope.() -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            if (title != null)
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(content = content)
        },
        confirmButton = {
            Button(onClick = onYesClick) {
                Text(yesButtonText)
            }
        },
        dismissButton = {
            Button(onClick = onNoClick) {
                Text(noButtonText)
            }
        },
    )
}