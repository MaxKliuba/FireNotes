package com.android.maxclub.firenotes.feature.notes.presentation.add_edit_note

import com.android.maxclub.firenotes.feature.notes.domain.models.Note

data class AddEditNoteUiState(
    val isLoading: Boolean,
    val note: Note?,
)