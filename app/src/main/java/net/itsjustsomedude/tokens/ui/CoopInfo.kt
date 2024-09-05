package net.itsjustsomedude.tokens.ui

import android.app.Application
import android.text.format.DateFormat
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.itsjustsomedude.tokens.SimpleDialogs
import net.itsjustsomedude.tokens.models.CoopViewModel
import net.itsjustsomedude.tokens.models.ModelFactory

@Composable
fun CoopInfo(
    coopId: Long,
    modifier: Modifier = Modifier,
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModelFactory = ModelFactory(application, coopId)
    val model: CoopViewModel = viewModel(factory = viewModelFactory)

    val context = LocalContext.current as? ComponentActivity
    val dateFormatter = remember {
        DateFormat.getDateFormat(context)
    }

    val timeFormatter = remember {
        DateFormat.getTimeFormat(context)
    }

    val modelCoop by model.coop.observeAsState()
    modelCoop?.let { coop ->
        Column {
            Row {
                Text(
                    text = "Coop: ${coop.name}",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = {
                    SimpleDialogs.textPicker(context, "Coop Name", coop.name) {
                        coop.name = it
                        model.update(coop)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Coop Name"
                    )
                }
            }
            Row {
                Text(
                    text = "Coop: ${coop.contract}",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = {
                    SimpleDialogs.textPicker(context, "Contract ID", coop.contract) {
                        coop.contract = it
                        model.update(coop)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit KevID"
                    )
                }
            }
            Row {
                Button(onClick = {
//                    context.startActivity()
                }) {
                    Text(text = "Edit Events")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Tokens"
                    )
                }
            }
            Row {
                TimeButton(
                    "Start Date",
                    if (coop.startTime == null) "Set" else dateFormatter.format(coop.startTime.time),
                    onClick = {
                        SimpleDialogs.datePicker(context, coop.startTime) {
                            coop.startTime = it
                            model.update(coop)
                        }
                    })
                TimeButton(
                    "Start Time",
                    if (coop.startTime == null) "Set" else timeFormatter.format(coop.startTime.time),
                    onClick = {
                        SimpleDialogs.timePicker(context, coop.startTime) {
                            coop.startTime = it
                            model.update(coop)
                        }
                    }
                )
            }
            Row {
                TimeButton(
                    "End Date",
                    if (coop.endTime == null) "Set" else dateFormatter.format(coop.endTime.time),
                    onClick = {
                        SimpleDialogs.datePicker(context, coop.endTime) {
                            coop.endTime = it
                            model.update(coop)
                        }
                    })
                TimeButton(
                    "End Time",
                    if (coop.endTime == null) "Set" else timeFormatter.format(coop.endTime.time),
                    onClick = {
                        SimpleDialogs.timePicker(context, coop.endTime) {
                            coop.endTime = it
                            model.update(coop)
                        }
                    }
                )
            }
            Row {
                Text(text = if (coop.sinkMode) "Sink Mode" else "Normal Mode")

                Switch(checked = coop.sinkMode, onCheckedChange = {
                    println("Changed!")
                    coop.sinkMode = it
                    model.update(coop)
                })
            }
        }
    }
}

@Composable
fun TimeButton(label: String, btnText: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontSize = 12.sp)
        Button(onClick = onClick) {
            Text(text = btnText)
        }
    }
}
