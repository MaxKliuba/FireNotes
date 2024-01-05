package com.android.maxclub.firenotes.feature.notes.domain.repositories

import com.android.maxclub.firenotes.feature.notes.domain.models.NoteItem
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getNoteItems(): Flow<List<NoteItem>>

    suspend fun addNoteItem(noteItem: NoteItem)

    suspend fun updateNoteItemChecked(noteItemId: String, checked: Boolean)

    suspend fun updateNoteItemContent(noteItemId: String, content: String)

    suspend fun updateAllNoteItemPositions(vararg noteItems: NoteItem)

    suspend fun deleteNoteItemById(noteItemId: String)

    suspend fun deletePermanentlyNoteItem(noteItemId: String)

    suspend fun tryRestoreNoteItemById(noteItemId: String)

    suspend fun deleteAllNoteItems()
}