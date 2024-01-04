package com.android.maxclub.firenotes.feature.notes.domain.repositories

import com.android.maxclub.firenotes.feature.notes.domain.models.NoteItem
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getNoteItems(): Flow<List<NoteItem>>

    suspend fun addNoteItem(noteItem: NoteItem)

    suspend fun deleteNoteItemById(noteItemId: String)

    suspend fun deletePermanentlyNoteItemById(noteItemId: String)

    suspend fun tryRestoreNoteItemById(noteItemId: String)

    suspend fun deleteAllNoteItems()
}