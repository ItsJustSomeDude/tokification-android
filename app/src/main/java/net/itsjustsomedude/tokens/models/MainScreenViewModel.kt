package net.itsjustsomedude.tokens.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.NotificationHelper
import net.itsjustsomedude.tokens.NotificationService
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.network.UpdateChecker
import net.itsjustsomedude.tokens.store.PreferencesRepository

private const val TAG = "MainViewModel"

// TODO: This needs a Koin conversion
class MainScreenViewModel(
    private val preferencesRepo: PreferencesRepository,
    private val coopRepo: CoopRepository,
    private val notificationHelper: NotificationHelper,
    private val preferences: PreferencesRepository,
    private val updateChecker: UpdateChecker
) : ViewModel() {

    init {
        checkForUpdate()
    }

    val noteDebugger = preferences.notificationDebugger.getStateFlow(viewModelScope)
    val selectedCoopId = preferencesRepo.selectedCoop.getStateFlow(viewModelScope)
    val serviceEnabled = preferencesRepo.serviceEnable.getStateFlow(viewModelScope)

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
            val sinkMode =
                preferences.defaultCoopMode.getValue() == PreferencesRepository.DEFAULT_COOP_MODE_SINK

            val newId = coopRepo.insert(
                Coop(sinkMode = sinkMode)
            )
            setSelectedCoopId(newId)
        }
    }

    // TODO: Delete the events, too.
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

    private val _updateAvailable = MutableStateFlow(false)
    val updateAvailable: StateFlow<Boolean> = _updateAvailable

    private fun checkForUpdate() {
        viewModelScope.launch {
            val result = updateChecker.isNewVersionAvailable()
            result.onSuccess { isNewVersion ->
                if (isNewVersion) {
                    println("A new version is available!")
                    // Handle update notification (e.g., prompt user to update)
                    _updateAvailable.value = true
                } else {
                    println("You're on the latest version.")
                }
            }.onFailure { error ->
                println("Error checking for updates: ${error.message}")
            }
        }
    }

}