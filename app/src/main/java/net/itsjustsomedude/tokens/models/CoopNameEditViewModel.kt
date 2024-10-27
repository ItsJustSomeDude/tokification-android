package net.itsjustsomedude.tokens.models

import android.content.ClipboardManager
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import net.itsjustsomedude.tokens.ClipboardHelper
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
                coop.value = match.group(1) ?: ""
                kevId.value = match.group(2) ?: ""
                return
            }
        }

        clipboardValid.value = false
    }

    private fun updateValidity() {
        clipboardAvailable.value = clipboard.isText()

        clipboardValid.value = true
    }
}