package net.itsjustsomedude.tokens.ui

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeRow(
    modifier: Modifier = Modifier,
    date: Calendar?,
    dateLabel: String,
    timeLabel: String,
    unsetText: String,
    onChange: (date: Calendar?) -> Unit
) {
    var dateDialog by remember { mutableStateOf(false) }
    var timeDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current as? Context

    val dateFormatter = remember {
        DateFormat.getDateFormat(context)
    }

    val timeFormatter = remember {
        DateFormat.getTimeFormat(context)
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        LabeledButton(
            label = dateLabel,
            btnText = if (date == null) unsetText else dateFormatter.format(date.time),
            onClick = {
                dateDialog = true
            }
        )

        LabeledButton(
            label = timeLabel,
            btnText = if (date == null) unsetText else timeFormatter.format(date.time),
            onClick = {
                timeDialog = true
            }
        )
    }

    if (dateDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date?.timeInMillis ?: Calendar.getInstance().timeInMillis
        )

        DatePickerDialog(
            onDismissRequest = {
                dateDialog = false
            },
            confirmButton = {
                TextButton(onClick = {
                    val newDate = date ?: Calendar.getInstance()

                    if (datePickerState.selectedDateMillis != null) {
                        newDate.timeInMillis = datePickerState.selectedDateMillis!!
                    }

                    onChange(newDate)
                    dateDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    dateDialog = false
                }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (timeDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = date?.get(Calendar.HOUR) ?: Calendar.getInstance().get(Calendar.HOUR),
            initialMinute = date?.get(Calendar.MINUTE) ?: Calendar.getInstance()
                .get(Calendar.MINUTE),
        )

        AlertDialog(
            onDismissRequest = {
                timeDialog = false
            },
            dismissButton = {
                TextButton(onClick = { timeDialog = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val newDate = date ?: Calendar.getInstance()

                    newDate.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    newDate.set(Calendar.MINUTE, timePickerState.minute)

                    onChange(newDate)
                    timeDialog = false
                }) {
                    Text("OK")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@Composable
fun LabeledButton(label: String, btnText: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = label, fontSize = 12.sp)
        Button(onClick = onClick) {
            Text(text = btnText)
        }
    }
}
