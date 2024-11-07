package net.itsjustsomedude.tokens.models

import android.content.ClipboardManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import net.itsjustsomedude.tokens.ClipboardHelper
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.regex.Pattern

private val patterns = listOf(
	Pattern.compile("^https://eicoop-carpet\\.netlify\\.app/([^/]+)/([^/]+)/*$"),
	Pattern.compile("^https://eggcoop\\.org/coops/([^/]+)/([^/]+)/*$")
)

class CoopNameEditViewModel(
	initialName: String,
	initialKevId: String,
	private val clipboard: ClipboardHelper
) : ViewModel() {
	val coop = mutableStateOf(initialName)
	val kevId = mutableStateOf(initialKevId)

	val clipboardAvailable = mutableStateOf(false)
	val clipboardValid = mutableStateOf(true)

	private val clipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
		updateValidity()
	}

	init {
		// Begin watching for clip changes
		clipboard.manager.addPrimaryClipChangedListener(clipChangedListener)

		// Check on startup too.
		updateValidity()
	}

	override fun onCleared() {
		super.onCleared()
		clipboard.manager.removePrimaryClipChangedListener(clipChangedListener)
	}

	fun readClipboard() {
		val text = clipboard.getText() ?: return

		for (pattern in patterns) {
			val match = pattern.matcher(text)

			if (match.matches()) {
				clipboardValid.value = true
				kevId.value = match.group(1) ?: ""
				coop.value = match.group(2) ?: ""
				return
			}
		}

		clipboardValid.value = false
	}

	private fun updateValidity() {
		clipboardAvailable.value = clipboard.isText()

		clipboardValid.value = true
	}

	companion object {
		@Composable
		fun provide(key: String, initialName: String, initialKevId: String): CoopNameEditViewModel =
			koinViewModel(key = "CoopNameEdit_$key") {
				parametersOf(initialName, initialKevId)
			}
	}
}