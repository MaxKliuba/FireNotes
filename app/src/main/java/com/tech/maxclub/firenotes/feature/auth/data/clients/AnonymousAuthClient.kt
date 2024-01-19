package com.tech.maxclub.firenotes.feature.auth.data.clients

import android.content.Intent
import android.content.IntentSender
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tech.maxclub.firenotes.feature.auth.domain.exceptions.SignInException
import com.tech.maxclub.firenotes.feature.auth.domain.exceptions.SignOutException
import com.tech.maxclub.firenotes.feature.auth.domain.models.AuthClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AnonymousAuthClient @Inject constructor() : AuthClient {

    private val auth = Firebase.auth

    @Throws(SignInException::class)
    override suspend fun beginSignIn(): IntentSender? {
        try {
            auth.signInAnonymously().await()
        } catch (e: Exception) {
            throw if (e is CancellationException) e else SignInException(e.localizedMessage)
        }

        return null
    }

    @Throws(SignInException::class)
    override suspend fun signInWithIntent(intent: Intent) {
        throw SignInException("Not implemented")
    }

    @Throws(SignOutException::class)
    override suspend fun signOut() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            throw if (e is CancellationException) e else SignOutException(e.localizedMessage)
        }
    }
}