package net.itsjustsomedude.tokens.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.NotificationHelper
import net.itsjustsomedude.tokens.NotificationService
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.store.PreferencesRepository

private const val TAG = "MainViewModel"

class MainScreenViewModel(
    private val preferencesRepo: PreferencesRepository,
    private val coopRepo: CoopRepository,
    private val notificationHelper: NotificationHelper,
    private val preferences: PreferencesRepository
) : ViewModel() {

    val noteDebugger = preferences.notificationDebugger.getStateFlow(viewModelScope)
    val selectedCoopId = preferencesRepo.selectedCoop.getStateFlow(viewModelScope)

    fun setSelectedCoopId(id: Long) {
        viewModelScope.launch {
            preferencesRepo.selectedCoop.setValue(id)

            // TODO: Move this maybe? Maybe?
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

        // TODO: This is NOT the right place to put this...
        // Since notificationHelper is injected, I can run from almost anywhere I know the CoopID.
        viewModelScope.launch {
            notificationHelper.sendActions(selectedCoopId.value)
        }
    }
}