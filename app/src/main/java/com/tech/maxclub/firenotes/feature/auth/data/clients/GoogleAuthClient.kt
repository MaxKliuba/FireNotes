package com.tech.maxclub.firenotes.feature.auth.data.clients

import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tech.maxclub.firenotes.BuildConfig
import com.tech.maxclub.firenotes.feature.auth.domain.exceptions.SignInException
import com.tech.maxclub.firenotes.feature.auth.domain.exceptions.SignOutException
import com.tech.maxclub.firenotes.feature.auth.domain.models.AuthClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleAuthClient @Inject constructor(
    private val signInClient: SignInClient
) : AuthClient {

    private val auth = Firebase.auth
    private val signInRequest: BeginSignInRequest
        get() = BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

    @Throws(SignInException::class)
    override suspend fun beginSignIn(): IntentSender? =
        try {
            signInClient.beginSignIn(signInRequest).await()
                ?.pendingIntent?.intentSender
        } catch (e: Exception) {
            throw if (e is CancellationException) e else SignInException(e.localizedMessage)
        }

    @Throws(SignInException::class)
    override suspend fun signInWithIntent(intent: Intent) {
        val credential = signInClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

        try {
            auth.signInWithCredential(googleCredentials).await()
        } catch (e: Exception) {
            throw if (e is CancellationException) e else SignInException(e.localizedMessage)
        }
    }

    @Throws(SignOutException::class)
    override suspend fun signOut() {
        try {
            signInClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            throw if (e is CancellationException) e else SignOutException(e.localizedMessage)
        }
    }
}