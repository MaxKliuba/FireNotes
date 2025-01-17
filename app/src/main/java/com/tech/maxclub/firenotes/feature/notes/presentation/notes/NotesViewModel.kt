package com.tech.maxclub.firenotes.feature.notes.presentation.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.maxclub.firenotes.core.utils.update
import com.tech.maxclub.firenotes.feature.notes.domain.exceptions.NoteRepoException
import com.tech.maxclub.firenotes.feature.notes.domain.repositories.NoteRepository
import com.tech.maxclub.firenotes.feature.notes.domain.usecases.GetNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val getNotesUseCase: GetNotesUseCase,
) : ViewModel() {

    private val _uiState = mutableStateOf(
        NotesUiState(
            isLoading = true,
            notes = emptyList(),
            isUserProfileDialogVisible = false,
            isDeleteAccountDialogVisible = false,
            isAccountDeleting = false,
        )
    )
    val uiState: State<NotesUiState> = _uiState

    private val uiActionChannel = Channel<NotesUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    init {
        permanentlyDeleteMarkedNotes()
        getNotes()
    }

    fun showUserProfileDialog() {
        _uiState.update { it.copy(isUserProfileDialogVisible = true) }
    }

    fun hideUserProfileDialog() {
        _uiState.update { it.copy(isUserProfileDialogVisible = false) }
    }

    fun showDeleteAccountDialog() {
        _uiState.update { it.copy(isDeleteAccountDialogVisible = true) }
    }

    fun hideDeleteAccountDialog() {
        _uiState.update { it.copy(isDeleteAccountDialogVisible = false) }
    }

    fun updateNoteExpanded(noteId: String, isExpanded: Boolean) {
        viewModelScope.launch {
            try {
                noteRepository.updateNoteExpanded(noteId, isExpanded)
            } catch (e: NoteRepoException) {
                e.printStackTrace()
                uiActionChannel.send(NotesUiAction.ShowNotesErrorMessage(e.message.toString()))
            }
        }
    }

    fun reorderLocalNotes(fromIndex: Int, toIndex: Int) {
        try {
            val fromItem = _uiState.value.notes[fromIndex]
            val toItem = _uiState.value.notes[toIndex]

            val newFromItem = fromItem.copy(position = toItem.position)
            val newToItem = toItem.copy(position = fromItem.position)

            _uiState.update {
                it.copy(
                    notes = it.notes.toMutableList().apply {
                        set(toIndex, newFromItem)
                        set(fromIndex, newToItem)
                    }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun applyNotesReorder() {
        viewModelScope.launch {
            try {
                val noteIdWithPositions = _uiState.value.notes.map { it.id to it.position }
                noteRepository.updateAllNotesPositions(*noteIdWithPositions.toTypedArray())
            } catch (e: NoteRepoException) {
                e.printStackTrace()
                uiActionChannel.send(NotesUiAction.ShowNotesErrorMessage(e.message.toString()))
            }
        }
    }

    fun permanentlyDeleteAccount() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isAccountDeleting = true) }
                noteRepository.permanentlyDeleteAccount()
            } catch (e: NoteRepoException) {
                e.printStackTrace()
                uiActionChannel.send(NotesUiAction.ShowNotesErrorMessage(e.message.toString()))
            } finally {
                _uiState.update { it.copy(isAccountDeleting = false) }
            }
        }
    }

    private fun permanentlyDeleteMarkedNotes() {
        viewModelScope.launch {
            try {
                noteRepository.permanentlyDeleteMarkedNotes()
            } catch (e: NoteRepoException) {
                e.printStackTrace()
                uiActionChannel.send(NotesUiAction.ShowNotesErrorMessage(e.message.toString()))
            }
        }
    }

    private fun getNotes() {
        getNotesUseCase()
            .onStart {
                _uiState.update { it.copy(isLoading = true) }
            }
            .onEach { notes ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        notes = notes,
                    )
                }
            }
            .catch { e ->
                e.printStackTrace()
                uiActionChannel.send(NotesUiAction.ShowNotesErrorMessage(e.message.toString()))
            }
            .launchIn(viewModelScope)
    }
}