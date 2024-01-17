package com.tech.maxclub.firenotes.feature.auth.domain.models

import android.content.Intent
import android.content.IntentSender
import com.tech.maxclub.firenotes.feature.auth.domain.exceptions.SignInException
import com.tech.maxclub.firenotes.feature.auth.domain.exceptions.SignOutException
import kotlinx.coroutines.flow.StateFlow

interface AuthClient {

    val currentUser: StateFlow<User?>

    @Throws(SignInException::class)
    suspend fun beginSignIn(): IntentSender?

    @Throws(SignInException::class)
    suspend fun signInWithIntent(intent: Intent)

    @Throws(SignOutException::class)
    suspend fun signOut()
}