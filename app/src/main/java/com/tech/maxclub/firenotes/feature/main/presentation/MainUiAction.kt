package com.tech.maxclub.firenotes.feature.main.presentation

import android.content.IntentSender

sealed class MainUiAction {
    data class LaunchSignInIntent(val intentSender: IntentSender) : MainUiAction()
    data class ShowAuthErrorMessage(val errorMessage: String) : MainUiAction()
    data class ShowNoteDeletedMessage(val noteId: String) : MainUiAction()
    data class ShowNotesErrorMessage(val errorMessage: String) : MainUiAction()
}