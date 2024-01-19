package com.tech.maxclub.firenotes.feature.notes.data.repositories

import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.tech.maxclub.firenotes.feature.auth.domain.repositories.AuthRepository
import com.tech.maxclub.firenotes.feature.notes.data.dto.NoteDto
import com.tech.maxclub.firenotes.feature.notes.data.dto.NoteItemDto
import com.tech.maxclub.firenotes.feature.notes.data.mappers.toNote
import com.tech.maxclub.firenotes.feature.notes.data.mappers.toNoteDto
import com.tech.maxclub.firenotes.feature.notes.data.mappers.toNoteDtoItem
import com.tech.maxclub.firenotes.feature.notes.data.mappers.toNoteItem
import com.tech.maxclub.firenotes.feature.notes.data.mappers.toNoteWithItemsCount
import com.tech.maxclub.firenotes.feature.notes.domain.exceptions.NoteRepoException
import com.tech.maxclub.firenotes.feature.notes.domain.models.Note
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteItem
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteWithItemsCount
import com.tech.maxclub.firenotes.feature.notes.domain.repositories.NoteRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepository
) : NoteRepository {

    private val firestore = Firebase.firestore

    private val collectionPath: String
        get() = "$USERS_COLLECTION_NAME/${authRepository.currentUser.value?.id}/$NOTES_COLLECTION_NAME"

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
                                val itemsCount = try {
                                    document.reference.collection(ITEMS_COLLECTION_NAME)
                                        .get().await()
                                        .documents.mapNotNull {
                                            it.toObject<NoteItemDto>()?.toNoteItem(it.id)
                                        }.count()
                                } catch (e: Exception) {
                                    if (e is CancellationException) {
                                        throw e
                                    } else {
                                        e.printStackTrace()
                                        0
                                    }
                                }

                                document.toObject<NoteDto>()
                                    ?.toNoteWithItemsCount(document.id, itemsCount)
                            }
                        }.awaitAll().filterNotNull()

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

    @Throws(NoteRepoException::class)
    override suspend fun addNote(note: Note): String {
        return try {
            firestore.collection(collectionPath)
                .add(note.toNoteDto()).await().id
        } catch (e: Exception) {
            throw if (e is CancellationException) e else NoteRepoException(e.localizedMessage)
        }
    }

    @Throws(NoteRepoException::class)
    override suspend fun updateNoteTitle(noteId: String, title: String) {
        try {
            val documentRef = firestore.collection(collectionPath).document(noteId)

            firestore.batch()
                .update(documentRef, TITLE_FIELD, title)
                .withNoteTimestampUpdate(noteId)
                .commit().await()
        } catch (e: Exception) {
            throw if (e is CancellationException) e else NoteRepoException(e.localizedMessage)
        }
    }

    @Throws(NoteRepoException::class)
    override suspend fun updateAllNotesPositions(vararg notes: NoteWithItemsCount) {
        try {
            val batch = firestore.batch()

            notes.forEach { note ->
                val documentRef = firestore.collection(collectionPath).document(note.id)
                batch.update(documentRef, POSITION_FIELD, note.position)
            }

            batch.commit().await()
        } catch (e: Exception) {
            throw if (e is CancellationException) e else NoteRepoException(e.localizedMessage)
        }
    }

    @Throws(NoteRepoException::class)
    override suspend fun deleteNoteById(noteId: String) {
        try {
            firestore.collection(collectionPath)
                .document(noteId)
                .update(DELETED_FIELD, true)
                .await()
        } catch (e: Exception) {
            throw if (e is CancellationException) e else NoteRepoException(e.localizedMessage)
        }
    }

    @Throws(NoteRepoException::class)
    override suspend fun permanentlyDeleteMarkedNotes() {
        try {
            val notesCollection = firestore.collection(collectionPath)
                .whereEqualTo(DELETED_FIELD, true)
                .get()
                .await()

            permanentlyDeleteNotes(notesCollection)

        } catch (e: Exception) {
            throw if (e is CancellationException) e else NoteRepoException(e.localizedMessage)
        }
    }

    @Throws(NoteRepoException::class)
    override suspend fun tryRestoreNoteById(noteId: String) {
        try {
            firestore.collection(collectionPath)
                .document(noteId)
                .update(DELETED_FIELD, false)
                .await()
        } catch (e: Exception) {
            throw if (e is CancellationException) e else NoteRepoException(e.localizedMessage)
        }
    }

    /*
     * NoteItem
     */
    @Throws(NoteRepoException::class)
    override suspend fun addNoteItem(noteId: String, noteItem: NoteItem) {
        try {
            firestore.collection(collectionPath)
                .document(noteId)
                .collection(ITEMS_COLLECTION_NAME)
                .add(noteItem.toNoteDtoItem())
                .await()

            firestore.batch()
                .withNoteTimestampUpdate(noteId)
                .commit().await()
        } catch (e: Exception) {
            throw if (e is CancellationException) e else NoteRepoException(e.localizedMessage)
        }
    }

    @Throws(NoteRepoException::class)
    override suspend fun updateNoteItemChecked(
        noteId: String,
        noteItemId: String,
        checked: Boolean
    ) {
        try {
            val documentRef = firestore.collection(collectionPath)
                .document(noteId)
                .collection(ITEMS_COLLECTION_NAME)
                .document(noteItemId)

            firestore.batch()
                .update(documentRef, CHECKED_FIELD, checked)
                .withNoteTimestampUpdate(noteId)
                .commit().await()
        } catch (e: Exception) {
            throw if (e is CancellationException) e else NoteRepoException(e.localizedMessage)
        }
    }

    @Throws(NoteRepoException::class)
    override suspend fun updateNoteItemContent(
        noteId: String,
        noteItemId: String,
        content: String
    ) {
        try {
            val documentRef = firestore.collection(collectionPath)
                .document(noteId)
                .collection(ITEMS_COLLECTION_NAME)
                .document(noteItemId)

            firestore.batch()
                .update(documentRef, CONTENT_FIELD, content)
                .withNoteTimestampUpdate(noteId)
                .commit().await()
        } catch (e: Exception) {
            throw if (e is CancellationException) e else NoteRepoException(e.localizedMessage)
        }
    }

    @Throws(NoteRepoException::class)
    override suspend fun updateAllNoteItemsPositions(noteId: String, vararg noteItems: NoteItem) {
        try {
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
        } catch (e: Exception) {
            throw if (e is CancellationException) e else NoteRepoException(e.localizedMessage)
        }
    }

    @Throws(NoteRepoException::class)
    override suspend fun deleteNoteItemById(noteId: String, noteItemId: String) {
        try {
            val documentRef = firestore.collection(collectionPath)
                .document(noteId)
                .collection(ITEMS_COLLECTION_NAME)
                .document(noteItemId)

            firestore.batch()
                .update(documentRef, DELETED_FIELD, true)
                .withNoteTimestampUpdate(noteId)
                .commit().await()
        } catch (e: Exception) {
            throw if (e is CancellationException) e else NoteRepoException(e.localizedMessage)
        }
    }

    @Throws(NoteRepoException::class)
    override suspend fun permanentlyDeleteMarkedNoteItems(noteId: String) {
        try {
            val batch = firestore.batch()

            firestore.collection(collectionPath)
                .document(noteId)
                .collection(ITEMS_COLLECTION_NAME)
                .whereEqualTo(DELETED_FIELD, true)
                .get()
                .await()
                .documents
                .forEach { document ->
                    batch.delete(document.reference)
                }

            batch.commit().await()
        } catch (e: Exception) {
            throw if (e is CancellationException) e else NoteRepoException(e.localizedMessage)
        }
    }

    @Throws(NoteRepoException::class)
    override suspend fun tryRestoreNoteItemById(noteId: String, noteItemId: String) {
        try {
            val documentRef = firestore.collection(collectionPath)
                .document(noteId)
                .collection(ITEMS_COLLECTION_NAME)
                .document(noteItemId)

            firestore.batch()
                .update(documentRef, DELETED_FIELD, false)
                .withNoteTimestampUpdate(noteId)
                .commit().await()
        } catch (e: Exception) {
            throw if (e is CancellationException) e else NoteRepoException(e.localizedMessage)
        }
    }

    /*
     * Account
     */
    override suspend fun permanentlyDeleteAccount() {
        try {
            val notesCollection = firestore.collection(collectionPath).get().await()
            permanentlyDeleteNotes(notesCollection)

            authRepository.deleteCurrentUser()
        } catch (e: Exception) {
            throw if (e is CancellationException) e else NoteRepoException(e.localizedMessage)
        }
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

    private suspend fun permanentlyDeleteNotes(notesCollection: QuerySnapshot) {
        coroutineScope {
            notesCollection.documents.map { document ->
                async {
                    val collection =
                        document.reference.collection((ITEMS_COLLECTION_NAME))
                            .get()
                            .await()
                            .documents

                    firestore.runTransaction { transition ->
                        collection.forEach { collectionDocument ->
                            transition.delete(collectionDocument.reference)
                        }
                        transition.delete(document.reference)
                    }.await()
                }
            }.awaitAll()
        }
    }

    companion object {
        private const val USERS_COLLECTION_NAME = "users"
        private const val NOTES_COLLECTION_NAME = "notes"
        private const val ITEMS_COLLECTION_NAME = "items"
        private const val TITLE_FIELD = "title"
        private const val TIMESTAMP_FIELD = "timestamp"
        private const val POSITION_FIELD = "position"
        private const val DELETED_FIELD = "deleted"
        private const val CHECKED_FIELD = "checked"
        private const val CONTENT_FIELD = "content"
    }
}