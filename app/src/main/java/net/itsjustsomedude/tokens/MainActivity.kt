package net.itsjustsomedude.tokens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.itsjustsomedude.tokens.models.MainScreenViewModel
import net.itsjustsomedude.tokens.ui.CoopList
import net.itsjustsomedude.tokens.ui.EditCoop
import net.itsjustsomedude.tokens.ui.NotificationDebuggerDialog
import net.itsjustsomedude.tokens.ui.SettingsScreen
import net.itsjustsomedude.tokens.ui.theme.TokificationTheme
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    val coop by model.selectedCoopId.collectAsState()
    val coopList by model.coopsList.observeAsState()

    var showCoopListSheet by remember { mutableStateOf(false) }
    var showNotificationDebugger by remember { mutableStateOf(false) }

    Header(
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
        EditCoop(coopId = coop)

        // TODO: Move _all_ this to a preferences screen.
        HorizontalDivider()
        Text("Eventually, this will be moved to a Settings screen, but right now that's not added so I tacked them on here.")
        SettingsScreen()

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    navigation: @Composable () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    content: @Composable ColumnScope.() -> Unit
) {
    TokificationTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    navigationIcon = navigation,

                    actions = actions,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                )
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(
                        PaddingValues(
                            start = 8.dp,
                            end = 8.dp,
                            top = paddingValues.calculateTopPadding() + 8.dp,
                            bottom = paddingValues.calculateBottomPadding(),
                        )
                    )
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                content()
            }
        }
    }
}