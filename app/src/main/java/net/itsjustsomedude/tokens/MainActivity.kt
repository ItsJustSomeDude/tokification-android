package net.itsjustsomedude.tokens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.itsjustsomedude.tokens.models.MainScreenViewModel
import net.itsjustsomedude.tokens.ui.CoopList
import net.itsjustsomedude.tokens.ui.EditCoop
import net.itsjustsomedude.tokens.ui.Header
import net.itsjustsomedude.tokens.ui.NotificationDebuggerDialog
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: This should be an Onboarding thing. But I haven't built that yet...
        NotificationHelper.requestPermission(this)

        enableEdgeToEdge()
        setContent {
            Content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(model: MainScreenViewModel = koinViewModel()) {
    val context = LocalContext.current

    val notificationDebuggerEnabled by model.noteDebugger.collectAsState()
    val serviceEnabled by model.serviceEnabled.collectAsState(true)
    val coop by model.selectedCoopId.collectAsState()
    val coopList by model.coopsList.observeAsState()

    var showCoopListSheet by remember { mutableStateOf(false) }
    var showNotificationDebugger by remember { mutableStateOf(false) }

    Header(
        title = { Text(stringResource(R.string.app_name)) },
        actions = {
            IconButton(onClick = {
                model.refreshNotifications()
            }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }

            if (notificationDebuggerEnabled)
                IconButton(onClick = {
                    showNotificationDebugger = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Notification Debugger"
                    )
                }

            IconButton(onClick = {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }

            IconButton(onClick = {
                showCoopListSheet = true
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = "Create Coop"
                )
            }
        }
    ) {
        if (!serviceEnabled) {
            Box(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = Color.Yellow,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(8.dp)
                    .clickable {
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    },
            ) {
                Column {
                    Text(
                        text = "Service Not Running",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Service must be running to process notifications. Start the service from the Settings screen.",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }

        EditCoop(coopId = coop)

        if (showCoopListSheet)
            ModalBottomSheet(onDismissRequest = { showCoopListSheet = false }) {
                CoopList(
                    listPre = {
                        Button(modifier = Modifier.padding(8.dp),
                            onClick = {
                                model.createAndSelectCoop()
                                showCoopListSheet = false
                            }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "")
                            Text("Create Coop")
                        }
                    },
                    coops = coopList ?: emptyList(),
                    onSelect = {
                        model.setSelectedCoopId(it)
                        showCoopListSheet = false
                    },
                    onDelete = { id, deleteEvents ->
                        model.deleteCoopById(id, deleteEvents)
                    }
                )
            }

        if (showNotificationDebugger)
            NotificationDebuggerDialog(onDismissRequest = {
                showNotificationDebugger = false
            })
    }
}