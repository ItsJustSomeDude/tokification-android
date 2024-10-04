package net.itsjustsomedude.tokens.models

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// TODO: This may be bad. Really bad.
@Composable
fun provideDialogModel(initiallyVisible: Boolean = false): DialogModel {
    return viewModel<DialogModel>()
}

class DialogModel : ViewModel() {
    private val _key = mutableIntStateOf(1)

    val key
        get() = _key.intValue.toString()

    var visible by mutableStateOf(false)

    fun reset() {
        _key.intValue++
    }

    fun show() {
        reset()
        visible = true
    }

    fun hide() {
        visible = false
    }

    fun toggle() {
        if (visible)
            hide()
        else
            show()
    }
}