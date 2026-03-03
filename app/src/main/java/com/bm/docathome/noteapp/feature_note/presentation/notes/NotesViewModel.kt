package com.bm.docathome.noteapp.feature_note.presentation.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bm.docathome.noteapp.feature_note.domain.model.Note
import com.bm.docathome.noteapp.feature_note.domain.use_cases.NoteUseCases
import com.bm.docathome.noteapp.feature_note.domain.util.NoteOrder
import com.bm.docathome.noteapp.feature_note.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private val _state = mutableStateOf<NotesState>(NotesState())
    val state: State<NotesState> = _state

    private var recentlyDeletedNote: Note? = null

    private var getNotesJob: Job? = null

    init {
        getNotes(NoteOrder.Date(OrderType.Descending))
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.Order -> {
                if (state.value.noteOrder.orderType == event.noteOrder.orderType && state.value.noteOrder::class == event.noteOrder::class)
                    return
                getNotes(event.noteOrder)
            }
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(event.note)
                    recentlyDeletedNote = event.note
                }
            }
            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    noteUseCases.addNote(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null
                }
            }
            is NotesEvent.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }
            is NotesEvent.SearchNote ->{
                // Update the search query in the state
                _state.value = state.value.copy(
                    searchQuery = event.query
                )
                // Trigger a re-fetch of notes with the new search query
                getNotes(state.value.noteOrder)
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        // Cancel any existing note-fetching job to avoid conflicts
        getNotesJob?.cancel()
        getNotesJob = noteUseCases.getNotes(noteOrder)
            .onEach { notes ->// Filter the notes based on the current search query
                val filteredNotes = if (state.value.searchQuery.isBlank()) {
                    notes
                } else {
                    notes.filter {
                        it.title.contains(state.value.searchQuery, ignoreCase = true) || it.content.contains(state.value.searchQuery, ignoreCase = true)
                    }
                }
                // Update the UI state with the new list of notes and order
                _state.value = state.value.copy(
                    notes = filteredNotes,
                    noteOrder = noteOrder)
            }.launchIn(viewModelScope) // Launch the flow in the ViewModel's scope
    }
}
