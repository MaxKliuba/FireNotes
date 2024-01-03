package com.android.maxclub.firenotes.feature.nav.presentation

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.firenotes.feature.auth.domain.models.AuthClient
import com.android.maxclub.firenotes.feature.auth.domain.models.User
import com.android.maxclub.firenotes.feature.auth.domain.exceptions.SignInException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavHostViewModel @Inject constructor(
    private val authClient: AuthClient
) : ViewModel() {

    private val uiActionChannel = Channel<NavHostUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    val currentUser: StateFlow<User?>
        get() = authClient.currentUser

    fun beginSignIn() {
        viewModelScope.launch {
            try {
                authClient.beginSignIn()?.let { intentSender ->
                    uiActionChannel.send(NavHostUiAction.LaunchSignInIntent(intentSender))
                }
            } catch (e: SignInException) {
                e.printStackTrace()
                uiActionChannel.send(NavHostUiAction.ShowAuthErrorMessage(e.message.toString()))
            }

        }
    }

    fun signInWithIntent(intent: Intent) {
        viewModelScope.launch {
            try {
                authClient.signInWithIntent(intent)
            } catch (e: SignInException) {
                e.printStackTrace()
                uiActionChannel.send(NavHostUiAction.ShowAuthErrorMessage(e.message.toString()))
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                authClient.signOut()
            } catch (e: SignInException) {
                e.printStackTrace()
                uiActionChannel.send(NavHostUiAction.ShowAuthErrorMessage(e.message.toString()))
            }
        }
    }
}