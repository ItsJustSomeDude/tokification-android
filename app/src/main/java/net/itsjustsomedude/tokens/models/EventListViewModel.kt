package net.itsjustsomedude.tokens.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import net.itsjustsomedude.tokens.db.EventRepository

class EventListViewModel(application: Application) : AndroidViewModel(application) {
    private val eventRepo = EventRepository(application)

    private val selectedCoop = MutableLiveData<Pair<String, String>>()

    val events = selectedCoop.switchMap { pair ->
        liveData {
            emitSource(eventRepo.listEvents(pair.first, pair.second))
        }
    }

//    val event: LiveData<List<Event>> = liveData {
//        val events = eventRepo.listEvents()
//        emitSource(events)
//    }

    fun setSelectedCoop(coop: String, kevId: String) {
        selectedCoop.value = Pair(coop, kevId)
    }
}