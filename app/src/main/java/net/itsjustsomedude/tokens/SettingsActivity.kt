package net.itsjustsomedude.tokens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import net.itsjustsomedude.tokens.ui.SettingsScreen
import net.itsjustsomedude.tokens.ui.components.Header

private const val TAG = "SettingsActivity"

class SettingsActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			Header(
				title = { Text("Settings") },
				navigation = {
					IconButton(
						onClick = { finish() }
					) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = "Back"
						)
					}
				}
			) {
				SettingsScreen()
			}
		}
	}
}
