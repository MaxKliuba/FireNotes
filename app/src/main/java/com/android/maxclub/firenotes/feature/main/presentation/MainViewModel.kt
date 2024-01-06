package com.android.maxclub.firenotes.feature.main.presentation

import android.content.Intent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.firenotes.feature.auth.domain.exceptions.SignInException
import com.android.maxclub.firenotes.feature.auth.domain.models.AuthClient
import com.android.maxclub.firenotes.feature.auth.domain.models.User
import com.android.maxclub.firenotes.feature.notes.domain.repositories.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authClient: AuthClient,
    private val noteRepository: NoteRepository,
) : ViewModel() {

    private val uiActionChannel = Channel<MainUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    private val _isSigningIn = mutableStateOf(false)
    val isSigningIn: State<Boolean> = _isSigningIn

    val currentUser: StateFlow<User?>
        get() = authClient.currentUser

    fun beginSignIn() {
        viewModelScope.launch {
            try {
                _isSigningIn.value = true
                authClient.beginSignIn()?.let { intentSender ->
                    uiActionChannel.send(MainUiAction.LaunchSignInIntent(intentSender))
                }
            } catch (e: SignInException) {
                e.printStackTrace()
                uiActionChannel.send(MainUiAction.ShowAuthErrorMessage(e.message.toString()))
            } finally {
                _isSigningIn.value = false
            }
        }
    }

    fun signInWithIntent(intent: Intent) {
        viewModelScope.launch {
            try {
                _isSigningIn.value = true
                authClient.signInWithIntent(intent)
            } catch (e: SignInException) {
                e.printStackTrace()
                uiActionChannel.send(MainUiAction.ShowAuthErrorMessage(e.message.toString()))
            } finally {
                _isSigningIn.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                authClient.signOut()
            } catch (e: SignInException) {
                e.printStackTrace()
                uiActionChannel.send(MainUiAction.ShowAuthErrorMessage(e.message.toString()))
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            noteRepository.deletePermanentlyNote(noteId)
        }
    }
}