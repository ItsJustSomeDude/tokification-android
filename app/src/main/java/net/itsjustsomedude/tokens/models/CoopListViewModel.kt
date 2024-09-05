package net.itsjustsomedude.tokens.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.store.StoreRepo

private const val TAG = "CoopListViewModel"

class CoopListViewModel(application: Application) : AndroidViewModel(application) {
    private val storeRepo = StoreRepo(application)
    private val coopRepo = CoopRepository(application)

    private val _coops = MutableLiveData<List<Coop>>(emptyList())
    val coops: LiveData<List<Coop>> = _coops

    init {
        println("Init Called")
        viewModelScope.launch {
            coopRepo.listCoops().observeForever { coops ->
                _coops.value = coops
                Log.i(TAG, "List of Coops changed: ${_coops.value}")
            }
        }
    }

    fun insert(coop: Coop) {
        viewModelScope.launch {
            coopRepo.insert(coop)
            Log.i(TAG, "Created a Coop.")
        }
    }

    fun setSelected(id: Long) {
        viewModelScope.launch {
            storeRepo.setSelectedCoop(id)
        }
    }

}