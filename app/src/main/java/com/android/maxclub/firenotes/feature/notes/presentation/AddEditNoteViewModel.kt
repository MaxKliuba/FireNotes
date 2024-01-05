package com.android.maxclub.firenotes.feature.notes.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.firenotes.core.utils.debounce
import com.android.maxclub.firenotes.core.utils.update
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteItem
import com.android.maxclub.firenotes.feature.notes.domain.repositories.NoteRepository
import com.android.maxclub.firenotes.feature.notes.domain.usecases.GetNoteItemsUseCase
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
    private val noteRepository: NoteRepository,
    private val getNoteItemsUseCase: GetNoteItemsUseCase,
) : ViewModel() {

    private val _uiState = mutableStateOf(
        AddEditNoteUiState(
            isLoading = true,
            noteItems = emptyList(),
        )
    )
    val uiState: State<AddEditNoteUiState> = _uiState

    private val onUpdateNoteItemContentWithDebounce: (String, String) -> Unit =
        viewModelScope.debounce(timeoutMillis = 1000L) { noteItemId, content ->
            noteRepository.updateNoteItemContent(noteItemId, content)
        }

    init {
        getNoteItems()
    }

    fun addNoteItem() {
        viewModelScope.launch {
            val noteItem = NoteItem(
                checked = false,
                content = "",
                position = Date().time,
            )
            noteRepository.addNoteItem(noteItem)
        }
    }

    fun updateNoteItemChecked(noteItemId: String, isChecked: Boolean) {
        viewModelScope.launch {
            noteRepository.updateNoteItemChecked(noteItemId, isChecked)
        }
    }

    fun updateNoteItemContent(noteItemId: String, content: String) {
        onUpdateNoteItemContentWithDebounce(noteItemId, content)
    }

    fun reorderLocalNoteItems(fromIndex: Int, toIndex: Int) {
        try {
            val fromItem = _uiState.value.noteItems[fromIndex]
            val toItem = _uiState.value.noteItems[toIndex]

            val newFromItem = fromItem.copy(position = toItem.position)
            val newToItem = toItem.copy(position = fromItem.position)

            _uiState.update {
                it.copy(
                    noteItems = it.noteItems.toMutableList().apply {
                        set(toIndex, newFromItem)
                        set(fromIndex, newToItem)
                    }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun applyNoteItemsReorder() {
        viewModelScope.launch {
            noteRepository.updateAllNoteItemPositions(*_uiState.value.noteItems.toTypedArray())
        }
    }

    fun deleteNoteItem(noteItemId: String) {
        viewModelScope.launch {
            noteRepository.deletePermanentlyNoteItem(noteItemId)
        }
    }

    fun deleteAllNoteItems() {
        viewModelScope.launch {
            noteRepository.deleteAllNoteItems()
        }
    }

    private fun getNoteItems() {
        getNoteItemsUseCase()
            .onStart {
                _uiState.update { it.copy(isLoading = true) }
            }
            .onEach { noteItems ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        noteItems = noteItems,
                    )
                }
            }
            .catch {
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }
}