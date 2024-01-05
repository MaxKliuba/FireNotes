package com.android.maxclub.firenotes.feature.notes.presentation

import androidx.compose.runtime.Stable
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteItem

data class AddEditNoteUiState(
    val isLoading: Boolean,
    @Stable val noteItems: List<NoteItem>,
)