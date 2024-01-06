package com.android.maxclub.firenotes.feature.notes.presentation.add_edit_note

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.firenotes.core.utils.debounce
import com.android.maxclub.firenotes.core.utils.update
import com.android.maxclub.firenotes.feature.main.presentation.Screen
import com.android.maxclub.firenotes.feature.notes.domain.models.Note
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteItem
import com.android.maxclub.firenotes.feature.notes.domain.repositories.NoteRepository
import com.android.maxclub.firenotes.feature.notes.domain.usecases.GetNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository,
    private val getNoteUseCase: GetNoteUseCase,
) : ViewModel() {

    private val initNoteId: String = savedStateHandle[Screen.AddEditNote.ARG_NOTE_ID]
        ?: Screen.AddEditNote.DEFAULT_NOTE_ID

    private val _uiState = mutableStateOf(
        AddEditNoteUiState(
            isLoading = true,
            note = null,
        )
    )
    val uiState: State<AddEditNoteUiState> = _uiState

    private val onUpdateNoteTitleWithDebounce: (String, String) -> Unit =
        viewModelScope.debounce(timeoutMillis = 500L) { noteId, title ->
            noteRepository.updateNoteTitle(noteId, title)
        }

    private val onUpdateNoteItemContentWithDebounce: (String, String, String) -> Unit =
        viewModelScope.debounce(timeoutMillis = 1000L) { noteId, noteItemId, content ->
            noteRepository.updateNoteItemContent(noteId, noteItemId, content)
        }

    init {
        getNote(initNoteId)
    }

    fun updateNoteTitle(noteId: String, title: String) {
        onUpdateNoteTitleWithDebounce(noteId, title)
    }

    fun addNoteItem() {
        _uiState.value.note?.let { note ->
            viewModelScope.launch {
                val noteItem = NoteItem(
                    checked = false,
                    content = "",
                    position = Date().time,
                )
                noteRepository.addNoteItem(note.id, noteItem)
            }
        }
    }

    fun updateNoteItemChecked(noteItemId: String, isChecked: Boolean) {
        _uiState.value.note?.let { note ->
            viewModelScope.launch {
                noteRepository.updateNoteItemChecked(note.id, noteItemId, isChecked)
            }
        }
    }

    fun updateNoteItemContent(noteItemId: String, content: String) {
        _uiState.value.note?.let { note ->
            onUpdateNoteItemContentWithDebounce(note.id, noteItemId, content)
        }
    }

    fun reorderLocalNoteItems(fromIndex: Int, toIndex: Int) {
        _uiState.value.note?.let { note ->
            try {
                val fromItem = note.items[fromIndex]
                val toItem = note.items[toIndex]

                val newFromItem = fromItem.copy(position = toItem.position)
                val newToItem = toItem.copy(position = fromItem.position)

                _uiState.update {
                    it.copy(
                        note = note.copy(
                            items = note.items.toMutableList().apply {
                                set(toIndex, newFromItem)
                                set(fromIndex, newToItem)
                            }
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun applyNoteItemsReorder() {
        _uiState.value.note?.let { note ->
            viewModelScope.launch {
                noteRepository.updateAllNoteItemsPositions(note.id, *note.items.toTypedArray())
            }
        }
    }

    fun deleteNoteItem(noteItemId: String) {
        _uiState.value.note?.let { note ->
            viewModelScope.launch {
                noteRepository.deletePermanentlyNoteItem(note.id, noteItemId)
            }
        }
    }

    private fun getNote(noteId: String) {
        println(noteId)
        viewModelScope.launch {
            val newNoteId = if (noteId == Screen.AddEditNote.DEFAULT_NOTE_ID) {
                val note = Note(
                    title = "",
                    timestamp = Date().time,
                    position = Date().time,
                )
                noteRepository.addNote(note)
            } else {
                noteId
            }

            getNoteUseCase(newNoteId)
                .onStart {
                    _uiState.update { it.copy(isLoading = true) }
                }
                .onEach { note ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            note = note,
                        )
                    }
                }
                .catch {
                    it.printStackTrace()
                }
                .launchIn(this)
        }
    }
}