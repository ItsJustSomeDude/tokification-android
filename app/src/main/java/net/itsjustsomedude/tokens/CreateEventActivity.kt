package net.itsjustsomedude.tokens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import net.itsjustsomedude.tokens.ui.EventEditDialog
import net.itsjustsomedude.tokens.ui.theme.TokificationTheme

class CreateEventActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val coopId = intent.getLongExtra(PARAM_COOP_ID, 2);

        setContent {
            TokificationTheme {
                val context = LocalContext.current

                EventEditDialog(
                    coopId = coopId,
                    eventId = null,
                    showExtendedButtons = false,
                    onDismiss = {
                        context.sendBroadcast(
                            NotificationActions.refreshNotificationIntent(
                                context,
                                coopId
                            )
                        )

                        finish()
                    }
                )

//                CreateEventContent(
//                    coopId = coopId,
//                    scope = lifecycleScope,
//                    onDismiss = {
//                        context.sendBroadcast(
//                            NotificationActions.refreshNotificationIntent(
//                                context,
//                                coopId
//                            )
//                        )
//
//                        finish()
//                    }
//                )
            }
        }
    }

    companion object {
        const val PARAM_COOP_ID = "CoopId"

        fun createIntent(ctx: Context, coopId: Long) =
            Intent(ctx, CreateEventActivity::class.java).putExtra(PARAM_COOP_ID, coopId)
    }
}

//@Composable
//fun CreateEventContent(
//    coopId: Long,
//    scope: CoroutineScope,
//    onDismiss: () -> Unit,
//    model: CreateEventViewModel = koinViewModel(),
//    // TODO: Make this ViewModel have params...
//    // Actually... this doesn't get a ViewModel. Make the composable have its own.
//) {
//    LaunchedEffect(key1 = coopId) {
//        model.createEvent(coopId)
//
//        println("Setup stuff done.")
//    }
//
//    val modelCoop by model.selectedCoop.observeAsState()
//    val selectedEvent by model.selectedEvent.observeAsState()
//
//    println(modelCoop)
//
//    EventEditDialog(
//        event = if (modelCoop == null) null else selectedEvent,
//        players = if (modelCoop == null) emptyList() else modelCoop!!.players,
//        onChanged = {
//            model.updateSelectedEvent(it)
//        },
//        onDoneClicked = {
//            scope.launch {
//                model.saveSelectedEventCor()
//                onDismiss()
//            }
//        },
//        onDismissRequest = onDismiss,
//        showExtendedButtons = false
//    )
//}