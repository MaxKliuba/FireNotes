package com.tech.maxclub.firenotes.feature.notes.presentation.add_edit_note

import android.content.Intent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.maxclub.firenotes.core.utils.debounce
import com.tech.maxclub.firenotes.core.utils.sendIn
import com.tech.maxclub.firenotes.core.utils.update
import com.tech.maxclub.firenotes.feature.main.presentation.Screen
import com.tech.maxclub.firenotes.feature.notes.domain.exceptions.NoteRepoException
import com.tech.maxclub.firenotes.feature.notes.domain.models.Note
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteItem
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteItemType
import com.tech.maxclub.firenotes.feature.notes.domain.repositories.NoteRepository
import com.tech.maxclub.firenotes.feature.notes.domain.usecases.GetNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

private const val MAX_TITLE_LENGTH = 100
private const val MAX_CONTENT_LENGTH = 4096

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
            isFabOpen = false,
            note = null,
        )
    )
    val uiState: State<AddEditNoteUiState> = _uiState

    private val uiActionChannel = Channel<AddEditNoteUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    private val onUpdateNoteTitleWithDebounce: (String, String) -> Unit =
        viewModelScope.debounce(timeoutMillis = 500L) { noteId, title ->
            noteRepository.updateNoteTitle(noteId, title)
        }

    private val onUpdateNoteItemContentWithDebounce: (String, String, String) -> Unit =
        viewModelScope.debounce(timeoutMillis = 1000L) { noteId, noteItemId, content ->
            noteRepository.updateNoteItemContent(noteId, noteItemId, content)
        }

    init {
        permanentlyDeleteMarkedNoteItems(initNoteId)
        getNote(initNoteId)
    }

    fun tryUpdateNoteTitle(noteId: String, title: String): Boolean {
        if (title.length > MAX_TITLE_LENGTH) return false

        try {
            onUpdateNoteTitleWithDebounce(noteId, title)
        } catch (e: NoteRepoException) {
            e.printStackTrace()
            uiActionChannel.sendIn(
                AddEditNoteUiAction.ShowNoteErrorMessage(e.message.toString()),
                viewModelScope
            )
        }

        return true
    }

    fun setFabState(isOpen: Boolean) {
        _uiState.update { it.copy(isFabOpen = isOpen) }
    }

    fun addNoteItem(type: NoteItemType) {
        _uiState.value.note?.let { note ->
            viewModelScope.launch {
                val noteItem = when (type) {
                    NoteItemType.TEXT -> NoteItem.Text(
                        content = "",
                        position = Date().time,
                    )

                    NoteItemType.TODO -> NoteItem.ToDo(
                        checked = false,
                        content = "",
                        position = Date().time,
                    )
                }

                try {
                    noteRepository.addNoteItem(note.id, noteItem)
                } catch (e: NoteRepoException) {
                    e.printStackTrace()
                    uiActionChannel.send(AddEditNoteUiAction.ShowNoteErrorMessage(e.message.toString()))
                }
            }

            setFabState(false)
        } ?: uiActionChannel.sendIn(
            AddEditNoteUiAction.ShowNoteErrorMessage("Note not found"),
            viewModelScope
        )
    }

    fun updateNoteItemChecked(noteItemId: String, isChecked: Boolean) {
        _uiState.value.note?.let { note ->
            viewModelScope.launch {
                try {
                    noteRepository.updateNoteItemChecked(note.id, noteItemId, isChecked)
                } catch (e: NoteRepoException) {
                    e.printStackTrace()
                    uiActionChannel.send(AddEditNoteUiAction.ShowNoteErrorMessage(e.message.toString()))
                }
            }
        } ?: uiActionChannel.sendIn(
            AddEditNoteUiAction.ShowNoteErrorMessage("Note not found"),
            viewModelScope
        )
    }

    fun tryUpdateNoteItemContent(noteItemId: String, content: String): Boolean {
        if (content.length > MAX_CONTENT_LENGTH) return false

        _uiState.value.note?.let { note ->
            try {
                onUpdateNoteItemContentWithDebounce(note.id, noteItemId, content)
            } catch (e: NoteRepoException) {
                e.printStackTrace()
                uiActionChannel.sendIn(
                    AddEditNoteUiAction.ShowNoteErrorMessage(e.message.toString()),
                    viewModelScope
                )
            }
        } ?: uiActionChannel.sendIn(
            AddEditNoteUiAction.ShowNoteErrorMessage("Note not found"),
            viewModelScope
        )

        return true
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
        } ?: uiActionChannel.sendIn(
            AddEditNoteUiAction.ShowNoteErrorMessage("Note not found"),
            viewModelScope
        )
    }

    fun applyNoteItemsReorder() {
        _uiState.value.note?.let { note ->
            viewModelScope.launch {
                try {
                    noteRepository.updateAllNoteItemsPositions(note.id, *note.items.toTypedArray())
                } catch (e: NoteRepoException) {
                    e.printStackTrace()
                    uiActionChannel.send(AddEditNoteUiAction.ShowNoteErrorMessage(e.message.toString()))
                }
            }
        } ?: uiActionChannel.sendIn(
            AddEditNoteUiAction.ShowNoteErrorMessage("Note not found"),
            viewModelScope
        )
    }

    fun deleteNoteItem(noteItemId: String) {
        _uiState.value.note?.let { note ->
            viewModelScope.launch {
                try {
                    noteRepository.deleteNoteItemById(note.id, noteItemId)
                    uiActionChannel.send(AddEditNoteUiAction.ShowNoteItemDeletedMessage(noteItemId))
                } catch (e: NoteRepoException) {
                    e.printStackTrace()
                    uiActionChannel.send(AddEditNoteUiAction.ShowNoteErrorMessage(e.message.toString()))
                }
            }
        } ?: uiActionChannel.sendIn(
            AddEditNoteUiAction.ShowNoteErrorMessage("Note not found"),
            viewModelScope
        )
    }

    fun tryRestoreNoteItem(noteItemId: String) {
        _uiState.value.note?.let { note ->
            viewModelScope.launch {
                try {
                    noteRepository.tryRestoreNoteItemById(note.id, noteItemId)
                } catch (e: NoteRepoException) {
                    e.printStackTrace()
                    uiActionChannel.send(AddEditNoteUiAction.ShowNoteErrorMessage(e.message.toString()))
                }
            }
        } ?: uiActionChannel.sendIn(
            AddEditNoteUiAction.ShowNoteErrorMessage("Note not found"),
            viewModelScope
        )
    }

    fun shareNote(note: Note) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, note.title)
            putExtra(Intent.EXTRA_TEXT, note.toContentString())
        }
        val chooserIntent = Intent.createChooser(intent, null)

        uiActionChannel.sendIn(
            AddEditNoteUiAction.LaunchShareNoteIntent(chooserIntent),
            viewModelScope,
        )
    }

    private fun permanentlyDeleteMarkedNoteItems(noteId: String) {
        if (noteId != Screen.AddEditNote.DEFAULT_NOTE_ID) {
            viewModelScope.launch {
                try {
                    noteRepository.permanentlyDeleteMarkedNoteItems(noteId)
                } catch (e: NoteRepoException) {
                    e.printStackTrace()
                    uiActionChannel.send(AddEditNoteUiAction.ShowNoteErrorMessage(e.message.toString()))
                }
            }
        }
    }

    private fun getNote(noteId: String) {
        viewModelScope.launch {
            val newNoteId = if (noteId == Screen.AddEditNote.DEFAULT_NOTE_ID) {
                val note = Note(
                    title = "",
                    timestamp = Date().time,
                    position = Date().time,
                )
                try {
                    noteRepository.addNote(note)
                } catch (e: NoteRepoException) {
                    e.printStackTrace()
                    uiActionChannel.send(AddEditNoteUiAction.ShowNoteErrorMessage(e.message.toString()))
                    null
                }
            } else {
                noteId
            }

            newNoteId?.let {
                getNoteUseCase(newNoteId)
                    .onStart {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    .onEach { note ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                note = note,
                                isFabOpen = note.items.isEmpty(),
                            )
                        }
                    }
                    .catch { e ->
                        e.printStackTrace()
                        uiActionChannel.send(AddEditNoteUiAction.ShowNoteErrorMessage(e.message.toString()))
                    }
                    .launchIn(this)
            }
        }
    }
}