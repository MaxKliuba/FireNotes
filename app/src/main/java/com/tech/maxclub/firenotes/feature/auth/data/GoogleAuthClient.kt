package com.tech.maxclub.firenotes.feature.auth.data

import android.content.Intent
import android.content.IntentSender
import com.tech.maxclub.firenotes.BuildConfig
import com.tech.maxclub.firenotes.feature.auth.domain.models.AuthClient
import com.tech.maxclub.firenotes.feature.auth.domain.models.User
import com.tech.maxclub.firenotes.feature.auth.domain.exceptions.SignInException
import com.tech.maxclub.firenotes.feature.auth.domain.exceptions.SignOutException
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
import kotlin.jvm.Throws

class GoogleAuthClient @Inject constructor(
    private val signInClient: SignInClient
) : AuthClient {

    private val auth = Firebase.auth
    private val webClientId = BuildConfig.WEB_CLIENT_ID
    private val signInRequest: BeginSignInRequest =
        BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

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