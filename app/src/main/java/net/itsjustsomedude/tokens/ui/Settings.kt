package net.itsjustsomedude.tokens.ui

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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

    Text("Notification Service Control")
    Switch(checked = serviceEnabled, onCheckedChange = { newState ->
        // TODO: Find out if this is the best place to manage Context.
        model.setServiceEnabled(context, newState)
    })

    HorizontalDivider()

    Text("Auto Dismiss Notifications")
    Switch(checked = autoDismiss, onCheckedChange = { newState ->
        model.setAutoDismiss(newState)
    })

    HorizontalDivider()

    Text("Notification Debugger")
    Switch(checked = notificationDebuggerEnabled, onCheckedChange = { newState ->
        model.setNoteDebugger(newState)
    })

    HorizontalDivider()

    Text("Player Name (Janky Field, type very slowly...)")
    OutlinedTextField(
        value = playerName,
        onValueChange = {
            model.setPlayerName(it)
        }
    )

    HorizontalDivider()

    Text("Default Coop Mode")
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

    Text("Enable Error Reporting", fontWeight = FontWeight.Bold)
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