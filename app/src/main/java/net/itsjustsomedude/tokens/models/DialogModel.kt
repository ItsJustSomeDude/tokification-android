package net.itsjustsomedude.tokens.models

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * If multiple controllers are used in the same activity, a unique key **must** be provided.
 */
@Composable
fun provideDialogController(key: String): DialogControllerViewModel {
	return viewModel(key = key)
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
		_visible.value = true
	}

	fun hide() {
		reset()
		_visible.value = false
	}

	fun toggle() {
		if (_visible.value)
			hide()
		else
			show()
	}
}