package net.itsjustsomedude.tokens.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.CoopRepository

class CoopViewModel(
    application: Application,
    coopId: Long,
    private val coopRepo: CoopRepository = CoopRepository(application)
) : AndroidViewModel(application) {

    val coop: LiveData<Coop?> = liveData {
        val newCoop = coopRepo.getCoop(coopId)
        emitSource(newCoop)
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