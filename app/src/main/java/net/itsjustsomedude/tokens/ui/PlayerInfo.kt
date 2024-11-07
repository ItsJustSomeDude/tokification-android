package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PlayerInfo() {
	val playerInfoString = AnnotatedString.Builder().apply {
		withStyle(MaterialTheme.typography.titleLarge.toSpanStyle()) { append("Player Name") }
		append(" - ")

	}.toAnnotatedString()

	Column(Modifier.fillMaxWidth()) {
		Text(playerInfoString, style = MaterialTheme.typography.titleLarge)


	}
}

@Preview(showSystemUi = true)
@Composable
fun PreviewPlayerInfo() {
	PlayerInfo()
}