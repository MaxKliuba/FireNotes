package com.android.maxclub.firenotes.di

import android.content.Context
import com.android.maxclub.firenotes.feature.auth.data.GoogleAuthClient
import com.android.maxclub.firenotes.feature.auth.domain.models.AuthClient
import com.google.android.gms.auth.api.identity.Identity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthClientModule {

    @Provides
    @Singleton
    fun provideGoogleAuthModule(@ApplicationContext context: Context): AuthClient =
        GoogleAuthClient(Identity.getSignInClient(context))
}