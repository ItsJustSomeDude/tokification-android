package net.itsjustsomedude.tokens.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CoopViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CoopRepository = CoopRepository(application)

    // LiveData to observe a single Coop
    private val _coop = MutableLiveData<Coop?>()
    val coop: LiveData<Coop?> get() = _coop

    // LiveData to observe the list of all Coops
    private val _allCoops = MutableLiveData<List<CoopSummary>>()
    val allCoops: LiveData<List<CoopSummary>> get() = _allCoops

    fun insert(coop: Coop) {
        viewModelScope.launch {
            repository.insert(coop)
        }
    }

    fun update(coop: Coop) {
        viewModelScope.launch {
            repository.update(coop)
        }
    }

    fun delete(coop: Coop) {
        viewModelScope.launch {
            repository.delete(coop)
        }
    }

    fun deleteAllCoops() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun getCoop(id: Int) {
        viewModelScope.launch {
            repository.getCoop(id)?.observeForever { coop ->
                _coop.postValue(coop) // Update the LiveData with the fetched Coop
            }
        }
    }

    fun listCoops() {
        viewModelScope.launch {
            repository.listCoops().observeForever { coops ->
                _allCoops.postValue(coops) // Update the LiveData with the fetched Coop
            }
        }
    }
}