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
						// TODO: Get out of the composable function.
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