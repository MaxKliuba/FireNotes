package com.tech.maxclub.firenotes.feature.notes.domain.repositories

import com.tech.maxclub.firenotes.feature.notes.domain.exceptions.NoteRepoException
import com.tech.maxclub.firenotes.feature.notes.domain.models.Note
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteItem
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteWithItemsCount
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    /*
     * Note
     */
    fun getNotes(): Flow<List<NoteWithItemsCount>>

    fun getNoteById(noteId: String): Flow<Note>

    @Throws(NoteRepoException::class)
    suspend fun addNote(note: Note): String

    @Throws(NoteRepoException::class)
    suspend fun updateNoteTitle(noteId: String, title: String)

    @Throws(NoteRepoException::class)
    suspend fun updateAllNotesPositions(vararg notes: NoteWithItemsCount)

    @Throws(NoteRepoException::class)
    suspend fun deleteNoteById(noteId: String)

    @Throws(NoteRepoException::class)
    suspend fun tryRestoreNoteById(noteId: String)

    @Throws(NoteRepoException::class)
    suspend fun permanentlyDeleteMarkedNotes()

    /*
     * NoteItem
     */
    @Throws(NoteRepoException::class)
    suspend fun addNoteItem(noteId: String, noteItem: NoteItem)

    @Throws(NoteRepoException::class)
    suspend fun updateNoteItemChecked(noteId: String, noteItemId: String, checked: Boolean)

    @Throws(NoteRepoException::class)
    suspend fun updateNoteItemContent(noteId: String, noteItemId: String, content: String)

    @Throws(NoteRepoException::class)
    suspend fun updateAllNoteItemsPositions(noteId: String, vararg noteItems: NoteItem)

    @Throws(NoteRepoException::class)
    suspend fun deleteNoteItemById(noteId: String, noteItemId: String)

    @Throws(NoteRepoException::class)
    suspend fun tryRestoreNoteItemById(noteId: String, noteItemId: String)

    @Throws(NoteRepoException::class)
    suspend fun permanentlyDeleteMarkedNoteItems(noteId: String)
}