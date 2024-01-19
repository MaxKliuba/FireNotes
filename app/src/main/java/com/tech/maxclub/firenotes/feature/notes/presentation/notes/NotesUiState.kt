package com.tech.maxclub.firenotes.feature.notes.presentation.notes

import androidx.compose.runtime.Stable
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteWithItemsCount

data class NotesUiState(
    val isLoading: Boolean,
    @Stable val notes: List<NoteWithItemsCount>,
    val isUserProfileDialogVisible: Boolean,
    val isDeleteAccountDialogVisible: Boolean,
    val isAccountDeleting: Boolean,
)