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

    val clipboardCoop = mutableStateOf("")
    val clipboardKevId = mutableStateOf("")

    val isClipboardValid = mutableStateOf(false)

    private val clipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
        updateClipboardValidity()
    }

    init {
        readClipboard()
        // TODO: Change the way this whole thing and UI works, it spams too much.
        updateClipboardValidity()
        clipboard.manager.addPrimaryClipChangedListener(clipChangedListener)
    }

    override fun onCleared() {
        super.onCleared()
        clipboard.manager.removePrimaryClipChangedListener(clipChangedListener)
    }

    fun readClipboard() {
        isClipboardValid()

        coop.value = clipboardCoop.value
        kevId.value = clipboardKevId.value
    }

    private fun isClipboardValid(): Boolean {
        val text = clipboard.getText() ?: return false

        for (pattern in patterns) {
            val match = pattern.matcher(text)

            if (match.matches()) {
                clipboardKevId.value = match.group(1) ?: ""
                clipboardCoop.value = match.group(2) ?: ""
                return true
            }
        }
        return false
    }

    private fun updateClipboardValidity() {
        isClipboardValid.value = isClipboardValid()
    }
}