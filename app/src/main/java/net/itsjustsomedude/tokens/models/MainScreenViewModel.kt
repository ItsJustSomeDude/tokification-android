package net.itsjustsomedude.tokens.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.NotificationHelper
import net.itsjustsomedude.tokens.NotificationService
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.store.StoreRepo

private const val TAG = "MainViewModel"

class MainScreenViewModel(
    private val storeRepo: StoreRepo,
    private val coopRepo: CoopRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _selectedCoopId: StateFlow<Long> = storeRepo.selectedCoopState
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val selectedCoopId: LiveData<Long> = _selectedCoopId.asLiveData()

    fun setSelectedCoopId(id: Long) {
        viewModelScope.launch {
            storeRepo.setSelectedCoop(id)

            notificationHelper.sendActions(id)
        }
    }

    val coopsList: LiveData<List<Coop>> = liveData {
        val newCoops = coopRepo.listCoops()
        emitSource(newCoops)
    }

    fun createAndSelectCoop() {
        viewModelScope.launch {
            val newId = coopRepo.insert(Coop())
            setSelectedCoopId(newId)
        }
    }

    fun deleteCoopById(id: Long, deleteEvents: Boolean) {
        viewModelScope.launch {
            coopRepo.deleteById(id)
        }
    }

    fun refreshNotifications() {
        NotificationService.processAllNotifications()
    }
}