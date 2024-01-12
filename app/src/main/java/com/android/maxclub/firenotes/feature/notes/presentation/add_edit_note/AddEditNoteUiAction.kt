package com.android.maxclub.firenotes.feature.notes.presentation.add_edit_note

import android.content.Intent

sealed class AddEditNoteUiAction {
    data class LaunchShareNoteIntent(val intent: Intent) : AddEditNoteUiAction()
    data class ShowNoteItemDeletedMessage(val noteItemId: String) : AddEditNoteUiAction()
    data class ShowNoteErrorMessage(val errorMessage: String) : AddEditNoteUiAction()
}