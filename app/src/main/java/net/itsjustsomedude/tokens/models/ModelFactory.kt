package net.itsjustsomedude.tokens.models

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ModelFactory(
    private val application: Application,
    private val id: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(application, id) as T
        } else if (modelClass.isAssignableFrom(CoopViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CoopViewModel(application, id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}