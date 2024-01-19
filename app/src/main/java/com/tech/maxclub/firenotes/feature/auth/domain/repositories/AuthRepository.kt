package com.tech.maxclub.firenotes.feature.auth.domain.repositories

import android.content.Intent
import android.content.IntentSender
import com.tech.maxclub.firenotes.feature.auth.domain.exceptions.DeleteUserException
import com.tech.maxclub.firenotes.feature.auth.domain.exceptions.SignInException
import com.tech.maxclub.firenotes.feature.auth.domain.exceptions.SignOutException
import com.tech.maxclub.firenotes.feature.auth.domain.models.User
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    val currentUser: StateFlow<User?>

    @Throws(SignInException::class)
    suspend fun beginSignIn(isAnonymous: Boolean): IntentSender?

    @Throws(SignInException::class)
    suspend fun signInWithIntent(intent: Intent)

    @Throws(SignOutException::class)
    suspend fun signOut(isAnonymous: Boolean)

    @Throws(DeleteUserException::class)
    suspend fun deleteCurrentUser()
}