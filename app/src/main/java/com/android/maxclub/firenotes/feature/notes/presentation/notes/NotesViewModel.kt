package com.android.maxclub.firenotes.feature.notes.presentation.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.firenotes.core.utils.update
import com.android.maxclub.firenotes.feature.notes.domain.repositories.NoteRepository
import com.android.maxclub.firenotes.feature.notes.domain.usecases.GetNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
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
            isUserProfileDialogVisible = false,
            notes = emptyList(),
        )
    )
    val uiState: State<NotesUiState> = _uiState

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
            noteRepository.updateAllNotesPositions(*_uiState.value.notes.toTypedArray())
        }
    }

    private fun permanentlyDeleteMarkedNotes() {
        viewModelScope.launch {
            noteRepository.permanentlyDeleteMarkedNotes()
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
            .catch {
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }
}