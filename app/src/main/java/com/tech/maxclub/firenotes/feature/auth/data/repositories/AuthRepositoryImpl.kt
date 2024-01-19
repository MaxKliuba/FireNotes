package com.tech.maxclub.firenotes.feature.auth.data.repositories

import android.content.Intent
import android.content.IntentSender
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tech.maxclub.firenotes.di.AnonymousAuth
import com.tech.maxclub.firenotes.feature.auth.data.mappers.toUser
import com.tech.maxclub.firenotes.feature.auth.domain.exceptions.DeleteUserException
import com.tech.maxclub.firenotes.feature.auth.domain.exceptions.SignInException
import com.tech.maxclub.firenotes.feature.auth.domain.exceptions.SignOutException
import com.tech.maxclub.firenotes.feature.auth.domain.models.AuthClient
import com.tech.maxclub.firenotes.feature.auth.domain.models.User
import com.tech.maxclub.firenotes.feature.auth.domain.repositories.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authClient: AuthClient,
    @AnonymousAuth private val anonymousAuthClient: AuthClient,
) : AuthRepository {

    private val auth = Firebase.auth

    override val currentUser: StateFlow<User?>
        get() = callbackFlow {
            val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                trySend(firebaseAuth.currentUser?.toUser())
            }
            auth.addAuthStateListener(authStateListener)

            awaitClose {
                auth.removeAuthStateListener(authStateListener)
            }
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = auth.currentUser?.toUser()
        )

    @Throws(SignInException::class)
    override suspend fun beginSignIn(isAnonymous: Boolean): IntentSender? {
        return if (isAnonymous) anonymousAuthClient.beginSignIn() else authClient.beginSignIn()
    }

    @Throws(SignInException::class)
    override suspend fun signInWithIntent(intent: Intent) {
        authClient.signInWithIntent(intent)
    }

    @Throws(SignOutException::class)
    override suspend fun signOut(isAnonymous: Boolean) {
        if (isAnonymous) anonymousAuthClient.signOut() else authClient.signOut()
    }

    @Throws(DeleteUserException::class)
    override suspend fun deleteCurrentUser() {
        try {
            auth.currentUser?.delete()?.await()
        } catch (e: Exception) {
            throw if (e is CancellationException) e else DeleteUserException(e.localizedMessage)
        }
    }
}