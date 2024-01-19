package com.tech.maxclub.firenotes.di

import android.content.Context
import com.tech.maxclub.firenotes.feature.auth.data.clients.GoogleAuthClient
import com.tech.maxclub.firenotes.feature.auth.domain.models.AuthClient
import com.tech.maxclub.firenotes.feature.notes.data.repositories.NoteRepositoryImpl
import com.tech.maxclub.firenotes.feature.notes.domain.repositories.NoteRepository
import com.google.android.gms.auth.api.identity.Identity
import com.tech.maxclub.firenotes.feature.auth.data.clients.AnonymousAuthClient
import com.tech.maxclub.firenotes.feature.auth.data.repositories.AuthRepositoryImpl
import com.tech.maxclub.firenotes.feature.auth.domain.repositories.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AnonymousAuth


@Module
@InstallIn(SingletonComponent::class)
object AuthClientModule {

    @Provides
    @Singleton
    fun provideGoogleAuthClient(@ApplicationContext context: Context): AuthClient =
        GoogleAuthClient(Identity.getSignInClient(context))

    @Provides
    @Singleton
    @AnonymousAuth
    fun provideAnonymousAuthClient(): AuthClient =
        AnonymousAuthClient()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepository: AuthRepositoryImpl
    ): AuthRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NoteRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        noteRepository: NoteRepositoryImpl
    ): NoteRepository
}