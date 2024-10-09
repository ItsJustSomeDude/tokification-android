package net.itsjustsomedude.tokens.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TimePickerDialog(
//    onCancel: () -> Unit,
//    onConfirm: (Calendar) -> Unit,
//    modifier: Modifier = Modifier
//) {
//
//    val time = Calendar.getInstance()
//    time.timeInMillis = System.currentTimeMillis()
//
//    var mode: DisplayMode by remember { mutableStateOf(DisplayMode.Picker) }
//    val timeState: TimePickerState = rememberTimePickerState(
//        initialHour = time[Calendar.HOUR_OF_DAY],
//        initialMinute = time[Calendar.MINUTE],
//    )
//
//    fun onConfirmClicked() {
//        val cal = Calendar.getInstance()
//        cal.set(Calendar.HOUR_OF_DAY, timeState.hour)
//        cal.set(Calendar.MINUTE, timeState.minute)
//        cal.isLenient = false
//
//        onConfirm(cal)
//    }
//
//    // TimePicker does not provide a default TimePickerDialog, so we use our own PickerDialog:
//    // https://issuetracker.google.com/issues/288311426
//    PickerDialog(
//        modifier = modifier,
//        onDismissRequest = onCancel,
//        title = { Text("Select hour") },
//        buttons = {
//            DisplayModeToggleButton(
//                displayMode = mode,
//                onDisplayModeChange = { mode = it },
//            )
////            Spacer(Modifier.weight(1f))
//            TextButton(onClick = onCancel) {
//                Text("Cancel")
//            }
//            TextButton(onClick = ::onConfirmClicked) {
//                Text("Confirm")
//            }
//        },
//    ) {
//        val contentModifier = Modifier.padding(horizontal = 24.dp)
//        when (mode) {
//            DisplayMode.Picker -> TimePicker(modifier = contentModifier, state = timeState)
//            DisplayMode.Input -> TimeInput(modifier = contentModifier, state = timeState)
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun DisplayModeToggleButton(
//    displayMode: DisplayMode,
//    onDisplayModeChange: (DisplayMode) -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    when (displayMode) {
//        DisplayMode.Picker -> IconButton(
//            modifier = modifier,
//            onClick = { onDisplayModeChange(DisplayMode.Input) },
//        ) {
//            Icon(
//                imageVector = Icons.Default.Edit,
//                contentDescription = "",
//            )
//        }
//
//        DisplayMode.Input -> IconButton(
//            modifier = modifier,
//            onClick = { onDisplayModeChange(DisplayMode.Picker) },
//        ) {
//            Icon(
//                imageVector = Icons.Default.Refresh,
//                contentDescription = "",
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PickerDialog(
//    onDismissRequest: () -> Unit,
//    title: @Composable () -> Unit,
//    buttons: @Composable RowScope.() -> Unit,
//    modifier: Modifier = Modifier,
//    content: @Composable ColumnScope.() -> Unit,
//) {
//    AlertDialog(
//        modifier = modifier
//            .width(IntrinsicSize.Min)
//            .height(IntrinsicSize.Min),
//        onDismissRequest = onDismissRequest,
//        properties = DialogProperties(usePlatformDefaultWidth = false),
//    ) {
//        Surface(
//            shape = MaterialTheme.shapes.extraLarge,
//            tonalElevation = 6.dp,
//        ) {
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                // Title
//                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
//                    ProvideTextStyle(MaterialTheme.typography.labelLarge) {
//                        Box(
//                            modifier = Modifier
//                                .align(Alignment.Start)
//                                .padding(horizontal = 24.dp)
//                                .padding(top = 16.dp, bottom = 20.dp),
//                        ) {
//                            title()
//                        }
//                    }
//                }
//                // Content
//                CompositionLocalProvider(LocalContentColor provides AlertDialogDefaults.textContentColor) {
//                    content()
//                }
//                // Buttons
//                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
//                    ProvideTextStyle(MaterialTheme.typography.labelLarge) {
//                        // TODO This should wrap on small screens, but we can't use AlertDialogFlowRow as it is no public
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(bottom = 8.dp, end = 6.dp, start = 6.dp),
//                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
//                        ) {
//                            buttons()
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Cancel") }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("OK") }
                }
            }
        }
    }
}