package com.android.maxclub.firenotes.feature.notes.presentation.notes

import androidx.compose.runtime.Stable
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteWithItemsCount

data class NotesUiState(
    val isLoading: Boolean,
    val isUserProfileDialogVisible: Boolean,
    @Stable val notes: List<NoteWithItemsCount>,
)