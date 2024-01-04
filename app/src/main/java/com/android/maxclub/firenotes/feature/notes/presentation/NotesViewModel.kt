package com.android.maxclub.firenotes.feature.notes.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteItem
import com.android.maxclub.firenotes.feature.notes.domain.repositories.NoteRepository
import com.android.maxclub.firenotes.feature.notes.domain.usecases.GetNotesItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val getNotesItemsUseCase: GetNotesItemsUseCase,
) : ViewModel() {

    var notesState = mutableStateOf(emptyList<NoteItem>())

    init {
        getNotes()
    }

    fun addNoteItem(noteItem: NoteItem) {
        viewModelScope.launch {
            noteRepository.addNoteItem(noteItem)
        }
    }

    fun deleteAllNotes() {
        viewModelScope.launch {
            noteRepository.deleteAllNoteItems()
        }
    }

    private fun getNotes() {
        getNotesItemsUseCase()
            .onEach { notes ->
                notesState.value = notes
            }
            .launchIn(viewModelScope)
    }
}