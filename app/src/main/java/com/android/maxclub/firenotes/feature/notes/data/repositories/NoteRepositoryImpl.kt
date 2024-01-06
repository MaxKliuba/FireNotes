package com.android.maxclub.firenotes.feature.notes.data.repositories

import com.android.maxclub.firenotes.feature.auth.domain.models.AuthClient
import com.android.maxclub.firenotes.feature.notes.data.dto.NoteDto
import com.android.maxclub.firenotes.feature.notes.data.dto.NoteItemDto
import com.android.maxclub.firenotes.feature.notes.data.mappers.toNote
import com.android.maxclub.firenotes.feature.notes.data.mappers.toNoteDto
import com.android.maxclub.firenotes.feature.notes.data.mappers.toNoteDtoItem
import com.android.maxclub.firenotes.feature.notes.data.mappers.toNoteItem
import com.android.maxclub.firenotes.feature.notes.data.mappers.toNoteWithItemsCount
import com.android.maxclub.firenotes.feature.notes.domain.models.Note
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteItem
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteWithItemsCount
import com.android.maxclub.firenotes.feature.notes.domain.repositories.NoteRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val authClient: AuthClient
) : NoteRepository {

    private val firestore = Firebase.firestore
    private val collectionPath: String
        get() = "users/${authClient.currentUser.value?.id}/notes"

    private fun getNoteItems(noteId: String): Flow<List<NoteItem>> = callbackFlow {
        val snapshotListener = firestore.collection(collectionPath)
            .document(noteId)
            .collection(ITEMS_COLLECTION_NAME)
            .addSnapshotListener { querySnapshot, error ->
                querySnapshot?.documents?.let { documents ->
                    val noteItems = documents.mapNotNull { document ->
                        document.toObject<NoteItemDto>()?.toNoteItem(document.id)
                    }

                    trySend(noteItems)
                }

                error?.printStackTrace()
            }

        awaitClose {
            snapshotListener.remove()
        }
    }

    /*
     * Note
     */
    override fun getNotes(): Flow<List<NoteWithItemsCount>> = callbackFlow {
        val snapshotListener = firestore.collection(collectionPath)
            .addSnapshotListener { querySnapshot, error ->
                querySnapshot?.documents?.let { documents ->
                    val notes = documents.mapNotNull { document ->
                        document.toObject<NoteDto>()
                            ?.takeUnless { it.deleted }
                            ?.toNoteWithItemsCount(document.id, 0)
                    }
                    trySend(notes)
                }

                error?.printStackTrace()
            }

        awaitClose {
            snapshotListener.remove()
        }
    }

    override fun getNoteById(noteId: String): Flow<Note> = callbackFlow {
        val snapshotListener = firestore.collection(collectionPath)
            .document(noteId)
            .addSnapshotListener { querySnapshot, error ->
                querySnapshot?.let { document ->
                    val note = document.toObject<NoteDto>()?.toNote(document.id)
                    note?.let { trySend(it) }
                }

                error?.printStackTrace()
            }

        awaitClose {
            snapshotListener.remove()
        }
    }.combine(getNoteItems(noteId)) { note, items ->
        note.copy(items = items)
    }

    override suspend fun addNote(note: Note): String {
        return firestore.collection(collectionPath)
            .add(note.toNoteDto())
            .await()
            .id
    }

    override suspend fun updateNoteTitle(noteId: String, title: String) {
        firestore.collection(collectionPath)
            .document(noteId)
            .update(TITLE_FIELD, title)
            .await()
    }

    override suspend fun updateAllNotesPositions(vararg notes: NoteWithItemsCount) {
        val batch = firestore.batch()

        notes.forEach { note ->
            val documentRef = firestore.collection(collectionPath).document(note.id)
            batch.update(documentRef, POSITION_FIELD, note.position)
        }

        batch.commit().await()
    }

    override suspend fun deleteNoteById(noteId: String) {
        firestore.collection(collectionPath)
            .document(noteId)
            .update(DELETED_FIELD, true)
            .await()
    }

    override suspend fun deletePermanentlyNote(noteId: String) {
        firestore.collection(collectionPath)
            .document(noteId)
            .delete()
            .await()
    }

    override suspend fun tryRestoreNoteById(noteId: String) {
        firestore.collection(collectionPath)
            .document(noteId)
            .update(DELETED_FIELD, false)
            .await()
    }

    /*
     * NoteItem
     */
    override suspend fun addNoteItem(noteId: String, noteItem: NoteItem) {
        firestore.collection(collectionPath)
            .document(noteId)
            .collection(ITEMS_COLLECTION_NAME)
            .add(noteItem.toNoteDtoItem())
            .await()
    }

    override suspend fun updateNoteItemChecked(
        noteId: String,
        noteItemId: String,
        checked: Boolean
    ) {
        firestore.collection(collectionPath)
            .document(noteId)
            .collection(ITEMS_COLLECTION_NAME)
            .document(noteItemId)
            .update(CHECKED_FIELD, checked)
            .await()
    }

    override suspend fun updateNoteItemContent(
        noteId: String,
        noteItemId: String,
        content: String
    ) {
        firestore.collection(collectionPath)
            .document(noteId)
            .collection(ITEMS_COLLECTION_NAME)
            .document(noteItemId)
            .update(CONTENT_FIELD, content)
            .await()
    }

    override suspend fun updateAllNoteItemsPositions(noteId: String, vararg noteItems: NoteItem) {
        val batch = firestore.batch()

        noteItems.forEach { noteItem ->
            val documentRef = firestore.collection(collectionPath)
                .document(noteId)
                .collection(ITEMS_COLLECTION_NAME)
                .document(noteItem.id)
            batch.update(documentRef, POSITION_FIELD, noteItem.position)
        }

        batch.commit().await()
    }

    override suspend fun deleteNoteItemById(noteId: String, noteItemId: String) {
        firestore.collection(collectionPath)
            .document(noteId)
            .collection(ITEMS_COLLECTION_NAME)
            .document(noteItemId)
            .update(DELETED_FIELD, true)
            .await()
    }

    override suspend fun deletePermanentlyNoteItem(noteId: String, noteItemId: String) {
        firestore.collection(collectionPath)
            .document(noteId)
            .collection(ITEMS_COLLECTION_NAME)
            .document(noteItemId)
            .delete()
            .await()
    }

    override suspend fun tryRestoreNoteItemById(noteId: String, noteItemId: String) {
        firestore.collection(collectionPath)
            .document(noteId)
            .collection(ITEMS_COLLECTION_NAME)
            .document(noteItemId)
            .update(DELETED_FIELD, false)
            .await()
    }

    companion object {
        private const val ITEMS_COLLECTION_NAME = "items"
        private const val TITLE_FIELD = "title"
        private const val POSITION_FIELD = "position"
        private const val DELETED_FIELD = "deleted"
        private const val CHECKED_FIELD = "checked"
        private const val CONTENT_FIELD = "content"
    }
}