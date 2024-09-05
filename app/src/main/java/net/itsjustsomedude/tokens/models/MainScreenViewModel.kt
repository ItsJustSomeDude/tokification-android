package net.itsjustsomedude.tokens.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.db.EventRepository
import net.itsjustsomedude.tokens.store.StoreRepo

private const val TAG = "MainViewModel"

class MainScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val storeRepo = StoreRepo(application)
    private val coopRepo = CoopRepository(application)
    private val eventRepo = EventRepository(application)

    private val selectedCoopId: StateFlow<Long> = storeRepo.selectedCoop
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val selectedCoop: LiveData<Coop?> = selectedCoopId.asLiveData().switchMap { id ->
        coopRepo.getCoopSync(id)
    }

    fun setSelected(id: Long) {
        viewModelScope.launch {
            storeRepo.setSelectedCoop(id)
        }
    }

//    private fun fetchToSelectedCoop(id: Long) {
//        println("FetchToSelected: $id")
//        viewModelScope.launch {
//            val coopLiveData = coopRepo.getCoop(id)
//            _selectedCoop.removeSource(coopLiveData)
//            _selectedCoop.addSource(coopLiveData) { coop ->
//                println("Setting: $coop")
//                _selectedCoop.value = coop
//                println("FetchToSelected2")
//            }
//        }
//        println("FetchToSelected3... Never fires?")
////        _selectedCoop.value = coopRepo.blockingGetCoop(id).value
//    }

//    fun getCoop(id: Long): LiveData<Coop?> {
//        return _coops.map { coops ->
//            coops.firstOrNull { it.id == id }
//        }
//    }

    fun insert(coop: Coop) {
        viewModelScope.launch {
            coopRepo.insert(coop)
            Log.i(TAG, "Created a Coop.")
        }
    }

    fun update(coop: Coop) {
        println("Update")
        viewModelScope.launch {
            coopRepo.update(coop)
        }
    }

    fun delete(coop: Coop) {
        viewModelScope.launch {
            coopRepo.delete(coop)
        }
    }

    fun insertEvent() {
        viewModelScope.launch {
            eventRepo.insert(
                Event(
                    "thing123",
                    "other-coop-2025",
                    null,
                    3,
                    "cool_person",
                    Event.DIRECTION_SENT
                )
            )
        }
    }
}