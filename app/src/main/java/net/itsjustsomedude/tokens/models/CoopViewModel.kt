package net.itsjustsomedude.tokens.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.CoopRepository

class CoopViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val coopRepo: CoopRepository = CoopRepository(application)

    private val selectedCoop = MutableLiveData<Long?>()

    val coop = selectedCoop.switchMap { id ->
        liveData {
            // TODO: Analyze nullability of selectedCoop.
            emitSource(coopRepo.getCoop(id ?: 0))
        }
    }

    fun setSelectedCoop(id: Long?) {
        selectedCoop.value = id
    }

    fun insert(coop: Coop) {
        viewModelScope.launch {
            coopRepo.insert(coop)
        }
    }

    fun update(coop: Coop) {
        viewModelScope.launch {
            coopRepo.update(coop)
        }

    }

    fun delete(coop: Coop) {
        viewModelScope.launch {
            coopRepo.delete(coop)
        }
    }
}