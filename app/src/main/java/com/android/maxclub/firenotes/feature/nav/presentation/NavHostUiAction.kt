package com.android.maxclub.firenotes.feature.nav.presentation

import android.content.IntentSender

sealed class NavHostUiAction {
    data class LaunchSignInIntent(val intentSender: IntentSender) : NavHostUiAction()
    data class ShowAuthErrorMessage(val errorMessage: String) : NavHostUiAction()
}