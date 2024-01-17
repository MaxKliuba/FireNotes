package com.tech.maxclub.firenotes.feature.notes.presentation.add_edit_note

import com.tech.maxclub.firenotes.feature.notes.domain.models.Note

data class AddEditNoteUiState(
    val isLoading: Boolean,
    val isFabOpen: Boolean,
    val note: Note?,
)