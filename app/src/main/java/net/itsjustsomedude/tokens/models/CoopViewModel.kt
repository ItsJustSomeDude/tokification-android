package net.itsjustsomedude.tokens.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.CoopRepository

class CoopViewModel(
    application: Application,
    coopId: Long,
    private val coopRepo: CoopRepository = CoopRepository(application)
) : AndroidViewModel(application) {

//    private val _coopId = MutableLiveData<Long>(0)
//    val coop: LiveData<Coop?> = _coopId.switchMap { id ->
//        liveData {
//            coopRepo.getCoop(id)
//        }
//    }

//    private val _coopId = MutableLiveData<Long>(0)
//    val coop: MediatorLiveData<Coop?> = MediatorLiveData()

//    init {
//        coop.addSource(_coopId) { id ->
//            viewModelScope.launch {
//                coop.value = coopRepo.getCoop(id).asFlow().first()
//            }
//        }
//    }

//    fun setCoop(id: Long) {
//        _coopId.value = id
//    }

//    val coop = MediatorLiveData<Coop?>()
//
//    fun setCoop(id: Long) {
//        viewModelScope.launch {
//            val newCoop = coopRepo.getCoop(id)
//
//            coop.addSource(newCoop) { value ->
//                coop.value = value
//            }
//        }
//    }

    private val _coop = MediatorLiveData<Coop?>()
    val coop: LiveData<Coop?> = _coop

    init {
        viewModelScope.launch {
            val newCoop = coopRepo.getCoop(coopId)

            _coop.addSource(newCoop) { value ->
                _coop.value = value
            }
        }
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