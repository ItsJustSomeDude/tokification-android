package net.itsjustsomedude.tokens.models

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun provideDialogController(): DialogControllerViewModel {
    return viewModel<DialogControllerViewModel>()
}

class DialogControllerViewModel : ViewModel() {
    private val _key = mutableLongStateOf(1)

    val key
        get() = _key.longValue.toString()

    private val _visible = MutableStateFlow(false)
    val visible: StateFlow<Boolean> = _visible.asStateFlow()

    fun reset() {
        _key.longValue++
    }

    fun show() {
        reset()
        _visible.value = true
    }

    fun hide() {
        _visible.value = false
    }

    fun toggle() {
        if (_visible.value)
            hide()
        else
            show()
    }
}