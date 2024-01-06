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
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val authClient: AuthClient
) : NoteRepository {

    private val firestore = Firebase.firestore
    private val collectionPath: String
        get() = "users/${authClient.currentUser.value?.id}/notes"

    /*
     * Note
     */
    override fun getNotes(): Flow<List<NoteWithItemsCount>> = callbackFlow {
        val scope = this

        val snapshotListener = firestore.collection(collectionPath)
            .addSnapshotListener { querySnapshot, error ->
                querySnapshot?.documents?.let { documents ->
                    scope.launch {
                        val notes = documents.mapNotNull { document ->
                            async {
                                val itemsCount =
                                    document.reference.collection(ITEMS_COLLECTION_NAME)
                                        .get().await()
                                        .documents.mapNotNull { it.toObject<NoteItemDto>() }
                                        .count { !it.deleted }

                                document.toObject<NoteDto>()
                                    ?.takeUnless { it.deleted }
                                    ?.toNoteWithItemsCount(document.id, itemsCount)
                            }
                        }.awaitAll()
                            .filterNotNull()

                        trySend(notes)
                    }
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
        val documentRef = firestore.collection(collectionPath).document(noteId)
        firestore.batch()
            .update(documentRef, TITLE_FIELD, title)
            .withNoteTimestampUpdate(noteId)
            .commit().await()
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

        firestore.batch()
            .withNoteTimestampUpdate(noteId)
            .commit().await()
    }

    override suspend fun updateNoteItemChecked(
        noteId: String,
        noteItemId: String,
        checked: Boolean
    ) {
        val documentRef = firestore.collection(collectionPath)
            .document(noteId)
            .collection(ITEMS_COLLECTION_NAME)
            .document(noteItemId)
        firestore.batch()
            .update(documentRef, CHECKED_FIELD, checked)
            .withNoteTimestampUpdate(noteId)
            .commit().await()
    }

    override suspend fun updateNoteItemContent(
        noteId: String,
        noteItemId: String,
        content: String
    ) {
        val documentRef = firestore.collection(collectionPath)
            .document(noteId)
            .collection(ITEMS_COLLECTION_NAME)
            .document(noteItemId)
        firestore.batch()
            .update(documentRef, CONTENT_FIELD, content)
            .withNoteTimestampUpdate(noteId)
            .commit().await()
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

        batch.withNoteTimestampUpdate(noteId)
            .commit().await()
    }

    override suspend fun deleteNoteItemById(noteId: String, noteItemId: String) {
        val documentRef = firestore.collection(collectionPath)
            .document(noteId)
            .collection(ITEMS_COLLECTION_NAME)
            .document(noteItemId)
        firestore.batch()
            .update(documentRef, DELETED_FIELD, true)
            .withNoteTimestampUpdate(noteId)
            .commit().await()
    }

    override suspend fun deletePermanentlyNoteItem(noteId: String, noteItemId: String) {
        val documentRef = firestore.collection(collectionPath)
            .document(noteId)
            .collection(ITEMS_COLLECTION_NAME)
            .document(noteItemId)
        firestore.batch()
            .delete(documentRef)
            .withNoteTimestampUpdate(noteId)
            .commit().await()
    }

    override suspend fun tryRestoreNoteItemById(noteId: String, noteItemId: String) {
        val documentRef = firestore.collection(collectionPath)
            .document(noteId)
            .collection(ITEMS_COLLECTION_NAME)
            .document(noteItemId)
        firestore.batch()
            .update(documentRef, DELETED_FIELD, false)
            .withNoteTimestampUpdate(noteId)
            .commit().await()
    }

    /*
     * Utils
     */

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

    private fun WriteBatch.withNoteTimestampUpdate(noteId: String): WriteBatch {
        val documentRef = firestore.collection(collectionPath).document(noteId)

        return update(documentRef, TIMESTAMP_FIELD, Date().time)
    }


    companion object {
        private const val ITEMS_COLLECTION_NAME = "items"
        private const val TITLE_FIELD = "title"
        private const val TIMESTAMP_FIELD = "timestamp"
        private const val POSITION_FIELD = "position"
        private const val DELETED_FIELD = "deleted"
        private const val CHECKED_FIELD = "checked"
        private const val CONTENT_FIELD = "content"
    }
}