package net.itsjustsomedude.tokens.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.store.StoreRepo

private const val TAG = "CoopListViewModel"

class CoopListViewModel(application: Application) : AndroidViewModel(application) {
    private val storeRepo = StoreRepo(application)
    private val coopRepo = CoopRepository(application)

    val coops: LiveData<List<Coop>> = liveData {
        val newCoops = coopRepo.listCoops()
        emitSource(newCoops)
    }

    fun insert(coop: Coop) {
        viewModelScope.launch {
            coopRepo.insert(coop)
        }
    }

    fun setSelected(id: Long) {
        viewModelScope.launch {
            storeRepo.setSelectedCoop(id)
        }
    }

}