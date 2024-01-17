package com.tech.maxclub.firenotes.di

import android.content.Context
import com.tech.maxclub.firenotes.feature.auth.data.GoogleAuthClient
import com.tech.maxclub.firenotes.feature.auth.domain.models.AuthClient
import com.tech.maxclub.firenotes.feature.notes.data.repositories.NoteRepositoryImpl
import com.tech.maxclub.firenotes.feature.notes.domain.repositories.NoteRepository
import com.google.android.gms.auth.api.identity.Identity
import dagger.Binds
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
    fun provideGoogleAuthClient(@ApplicationContext context: Context): AuthClient =
        GoogleAuthClient(Identity.getSignInClient(context))
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