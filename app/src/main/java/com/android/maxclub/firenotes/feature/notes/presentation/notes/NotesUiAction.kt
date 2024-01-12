package com.android.maxclub.firenotes.feature.notes.presentation.notes

sealed class NotesUiAction {
    data class ShowNotesErrorMessage(val errorMessage: String) : NotesUiAction()
}