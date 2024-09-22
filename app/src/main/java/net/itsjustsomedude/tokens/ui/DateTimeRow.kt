package net.itsjustsomedude.tokens.ui

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.TimeZone

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
                    val initialDate = (date?.clone() as Calendar?) ?: Calendar.getInstance()

                    val newDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    datePickerState.selectedDateMillis?.let {
                        newDate.timeInMillis = it
                    }

                    initialDate.set(Calendar.YEAR, newDate.get(Calendar.YEAR))
                    initialDate.set(Calendar.MONTH, newDate.get(Calendar.MONTH))
                    initialDate.set(Calendar.DAY_OF_MONTH, newDate.get(Calendar.DAY_OF_MONTH))

                    onChange(initialDate)
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
            initialHour = date?.get(Calendar.HOUR_OF_DAY) ?: Calendar.getInstance()
                .get(Calendar.HOUR_OF_DAY),
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
fun DateTimeRowSkeleton(
    modifier: Modifier = Modifier,
    dateLabel: String = "Date",
    timeLabel: String = "Time",
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        LabeledButtonSkeleton(label = dateLabel)
        LabeledButtonSkeleton(label = timeLabel)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDateTimeRow() {
    DateTimeRow(
        date = Calendar.getInstance(),
        dateLabel = "Some Date",
        timeLabel = "Some Time",
        unsetText = "Set",
        onChange = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewUnsetDateTimeRow() {
    DateTimeRow(
        date = null,
        dateLabel = "Some Date",
        timeLabel = "Some Time",
        unsetText = "Set",
        onChange = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewDateTimeRowSkeleton() {
    DateTimeRowSkeleton()
}