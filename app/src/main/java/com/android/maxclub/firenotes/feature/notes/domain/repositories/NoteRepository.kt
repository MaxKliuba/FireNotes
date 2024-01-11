package com.android.maxclub.firenotes.feature.notes.domain.repositories

import com.android.maxclub.firenotes.feature.notes.domain.models.Note
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteItem
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteWithItemsCount
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    /*
     * Note
     */
    fun getNotes(): Flow<List<NoteWithItemsCount>>

    fun getNoteById(noteId: String): Flow<Note>

    suspend fun addNote(note: Note): String

    suspend fun updateNoteTitle(noteId: String, title: String)

    suspend fun updateAllNotesPositions(vararg notes: NoteWithItemsCount)

    suspend fun deleteNoteById(noteId: String)

    suspend fun tryRestoreNoteById(noteId: String)

    suspend fun permanentlyDeleteMarkedNotes()

    /*
     * NoteItem
     */
    suspend fun addNoteItem(noteId: String, noteItem: NoteItem)

    suspend fun updateNoteItemChecked(noteId: String, noteItemId: String, checked: Boolean)

    suspend fun updateNoteItemContent(noteId: String, noteItemId: String, content: String)

    suspend fun updateAllNoteItemsPositions(noteId: String, vararg noteItems: NoteItem)

    suspend fun deleteNoteItemById(noteId: String, noteItemId: String)

    suspend fun tryRestoreNoteItemById(noteId: String, noteItemId: String)

    suspend fun permanentlyDeleteMarkedNoteItems(noteId: String)
}