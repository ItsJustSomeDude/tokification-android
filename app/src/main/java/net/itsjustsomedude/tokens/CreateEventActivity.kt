package net.itsjustsomedude.tokens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.models.CreateEventViewModel
import net.itsjustsomedude.tokens.ui.EventEditDialog
import net.itsjustsomedude.tokens.ui.theme.TokificationTheme
import org.koin.compose.viewmodel.koinViewModel

class CreateEventActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val coopId = intent.getLongExtra(PARAM_COOP_ID, 2);

        setContent {
            TokificationTheme {
                val context = LocalContext.current

                CreateEventContent(
                    coopId = coopId,
                    scope = lifecycleScope,
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
            }
        }
    }

    companion object {
        const val PARAM_COOP_ID = "CoopId"

        fun createIntent(ctx: Context, coopId: Long) =
            Intent(ctx, CreateEventActivity::class.java).putExtra(PARAM_COOP_ID, coopId)
    }
}

@Composable
fun CreateEventContent(
    coopId: Long,
    scope: CoroutineScope,
    onDismiss: () -> Unit,
    model: CreateEventViewModel = koinViewModel(),
) {
    LaunchedEffect(key1 = coopId) {
        model.createEvent(coopId)

        println("Setup stuff done.")
    }

    val modelCoop by model.selectedCoop.observeAsState()
    val selectedEvent by model.selectedEvent.observeAsState()

    println(modelCoop)

    EventEditDialog(
        event = if (modelCoop == null) null else selectedEvent,
        players = if (modelCoop == null) emptyList() else modelCoop!!.players,
        onChanged = {
            model.updateSelectedEvent(it)
        },
        onDoneClicked = {
            scope.launch {
                model.saveSelectedEventCor()
                onDismiss()
            }
        },
        onDismissRequest = onDismiss,
        showExtendedButtons = false
    )
}