package net.itsjustsomedude.tokens.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import io.sentry.Sentry
import net.itsjustsomedude.tokens.BuildConfig
import net.itsjustsomedude.tokens.models.SettingsViewModel
import net.itsjustsomedude.tokens.store.PreferencesRepository
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
	model: SettingsViewModel = koinViewModel()
) {
	val context = LocalContext.current

	val serviceEnabled by model.serviceEnabled.collectAsState()
	val autoDismiss by model.autoDismiss.collectAsState()
	val notificationDebuggerEnabled by model.noteDebugger.collectAsState()
	val playerName by model.playerName.collectAsState()
	val defaultCoopMode by model.defaultCoopMode.collectAsState()
	val sentryEnabled by model.sentryEnabled.collectAsState()

	Text(text = "Notification Service Control", fontWeight = FontWeight.Bold)
	Text(
		text = "This enables or disables Tokification reading Egg Inc's notifications.  This is used to track tokens that you have received.  If enabled, it runs in the background, so it may be helpful to disable when you have no active co-ops.",
		fontStyle = FontStyle.Italic
	)
	Switch(checked = serviceEnabled, onCheckedChange = { newState ->
		// TODO: Find out if this is the best place to manage Context.
		model.setServiceEnabled(context, newState)
	})

	Text(
		text = "Setting Disabled?",
		fontWeight = FontWeight.Bold
	)
	Text(
		modifier = Modifier.clickable {
			//redirect user to app Settings
			val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
				addCategory(Intent.CATEGORY_DEFAULT)
				setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID))
			}
			context.startActivity(i)
		},
		text = "Click here to go to App Info, then click [...], then \"Allow Restricted Settings\".",
		fontStyle = FontStyle.Italic,
		textDecoration = TextDecoration.Underline
	)

	HorizontalDivider()

	Text(text = "Auto Dismiss Notifications", fontWeight = FontWeight.Bold)
	Text(
		text = "If enabled, Egg Inc's notifications will be dismissed automatically shortly after Tokification processes them.",
		fontStyle = FontStyle.Italic
	)
	Switch(checked = autoDismiss, onCheckedChange = { newState ->
		model.setAutoDismiss(newState)
	})

	HorizontalDivider()

	Text(text = "Notification Debugger", fontWeight = FontWeight.Bold)
	Text(
		text = "This adds a wrench icon to the main screen that allows you to send fake notifications.  Used for debugging, can be left disabled here.",
		fontStyle = FontStyle.Italic
	)
	Switch(checked = notificationDebuggerEnabled, onCheckedChange = { newState ->
		model.setNoteDebugger(newState)
	})

	HorizontalDivider()

	Text(text = "Player Name (Janky Field, type very slowly...)", fontWeight = FontWeight.Bold)
	Text(
		text = "This is your In-Game Name, used for showing in Sink Reports.",
		fontStyle = FontStyle.Italic
	)
	OutlinedTextField(
		value = playerName,
		onValueChange = {
			model.setPlayerName(it)
		}
	)

	HorizontalDivider()

	Text(text = "Default Coop Mode", fontWeight = FontWeight.Bold)
	Text(
		text = "Whether new Co-ops will be in Normal or Sink mode by default.",
		fontStyle = FontStyle.Italic
	)
	SingleChoiceSegmentedButtonRow {
		SegmentedButton(
			selected = defaultCoopMode == PreferencesRepository.DEFAULT_COOP_MODE_SINK,
			onClick = {
				model.setDefaultCoopMode(PreferencesRepository.DEFAULT_COOP_MODE_SINK)
			},
			shape = SegmentedButtonDefaults.itemShape(0, 2)
		) {
			Text("Sink")
		}

		SegmentedButton(
			selected = defaultCoopMode == PreferencesRepository.DEFAULT_COOP_MODE_NORMAL,
			onClick = {
				model.setDefaultCoopMode(PreferencesRepository.DEFAULT_COOP_MODE_NORMAL)
			},
			shape = SegmentedButtonDefaults.itemShape(1, 2)
		) {
			Text("Normal")
		}
	}

	HorizontalDivider()

	Text(text = "Enable Error Reporting", fontWeight = FontWeight.Bold)
	Text(
		text = "Tokification uses Sentry to report errors and crashes. Information about your device and your IGN entered above will be sent with error reports if enabled.",
		fontStyle = FontStyle.Italic
	)
	Switch(checked = sentryEnabled, onCheckedChange = { newState ->
		model.setSentryEnabled(newState)
	})

	if (sentryEnabled && BuildConfig.SENTRY_DSN.isNotBlank())
		Button(
			onClick = {
				Sentry.captureException(RuntimeException("This app uses Sentry! :)"));
				println("Crash!")
			}
		) {
			Text("Send blank report to Sentry (For Debugging)")
		}

}
