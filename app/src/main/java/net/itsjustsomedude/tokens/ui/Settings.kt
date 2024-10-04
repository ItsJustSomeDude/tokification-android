package net.itsjustsomedude.tokens.ui

import androidx.compose.material3.ExperimentalMaterial3Api
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

    Text("Notification Service Control")
    Switch(checked = serviceEnabled, onCheckedChange = { newState ->
        // TODO: Find out if this is the best place to manage Context.
        model.setServiceEnabled(context, newState)
    })

    Text("Auto Dismiss Notifications")
    Switch(checked = autoDismiss, onCheckedChange = { newState ->
        model.setAutoDismiss(newState)
    })

    Text("Notification Debugger")
    Switch(checked = notificationDebuggerEnabled, onCheckedChange = { newState ->
        model.setNoteDebugger(newState)
    })

    Text("Player Name (Janky Field, might fix later, idk.)")
    OutlinedTextField(
        value = playerName,
        onValueChange = {
            model.setPlayerName(it)
        }
    )

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
}