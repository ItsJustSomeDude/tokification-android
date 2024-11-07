package net.itsjustsomedude.tokens.models

import androidx.compose.runtime.Composable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.itsjustsomedude.tokens.db.Coop
import net.itsjustsomedude.tokens.db.CoopRepository
import net.itsjustsomedude.tokens.db.Event
import net.itsjustsomedude.tokens.db.EventRepository
import net.itsjustsomedude.tokens.updateInferredCoopValues
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class EventEditViewModel(
	private val coopId: Long,
	// If null, create event, otherwise edit.
	private val eventId: Long?,
	private val eventRepo: EventRepository,
	private val coopRepo: CoopRepository
) : ViewModel() {

	init {
		fetch()
	}

	val coop = MutableLiveData<Coop>()
	val event = MutableLiveData<Event>()

	private fun fetch() = viewModelScope.launch {
		try {
			// Fetch the location based on locationId
			val fetchedCoop = coopRepo.getCoop(coopId)
				?: throw Exception("Invalid Coop")
			coop.value = fetchedCoop

			// Decide based on the eventId
			if (eventId == null) {
				// Event ID is null, create a new event based on the location info
				val newEvent = eventRepo.newEvent(fetchedCoop)
				event.value = newEvent
			} else {
				// Event ID is provided, fetch the existing event
				val fetchedEvent = eventRepo.getEvent(eventId)
					?: throw Exception("Invalid Event")
				event.value = fetchedEvent
			}
		} catch (e: Exception) {
			// Handle error
		}
	}

	fun updateEvent(newEvent: Event) {
		event.value = newEvent
	}

	fun save() {
		viewModelScope.launch {
			event.value?.let {
				updateInferredCoopValues(it)
				eventRepo.upsert(it)
			}
		}
	}

	companion object {
		@Composable
		fun provide(key: String, coopId: Long, eventId: Long? = null): EventEditViewModel =
			koinViewModel(key = "EventEdit_$key") { parametersOf(coopId, eventId) }
	}
}