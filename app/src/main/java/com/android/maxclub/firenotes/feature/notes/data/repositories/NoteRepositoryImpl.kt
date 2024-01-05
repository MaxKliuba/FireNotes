package com.android.maxclub.firenotes.feature.notes.data.repositories

import com.android.maxclub.firenotes.feature.auth.domain.models.AuthClient
import com.android.maxclub.firenotes.feature.notes.data.dto.NoteItemDto
import com.android.maxclub.firenotes.feature.notes.data.mappers.toNoteDtoItem
import com.android.maxclub.firenotes.feature.notes.data.mappers.toNoteItem
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteItem
import com.android.maxclub.firenotes.feature.notes.domain.repositories.NoteRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val authClient: AuthClient
) : NoteRepository {

    private val firestore = Firebase.firestore
    private val collectionPath: String
        get() = "users/${authClient.currentUser.value?.id}/notes"

    override fun getNoteItems(): Flow<List<NoteItem>> = callbackFlow {
        val snapshotListener = firestore.collection(collectionPath)
            .addSnapshotListener { querySnapshot, error ->
                querySnapshot?.documents?.let { documents ->
                    val noteItems = documents.mapNotNull { document ->
                        document.toObject<NoteItemDto>()
                            ?.toNoteItem(document.id)
                    }

                    trySend(noteItems)
                }

                error?.printStackTrace()
            }

        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun addNoteItem(noteItem: NoteItem) {
        firestore.collection(collectionPath)
            .add(noteItem.toNoteDtoItem())
            .await()
    }

    override suspend fun updateNoteItemChecked(noteItemId: String, checked: Boolean) {
        firestore.collection(collectionPath)
            .document(noteItemId)
            .update("checked", checked)
            .await()
    }

    override suspend fun updateNoteItemContent(noteItemId: String, content: String) {
        firestore.collection(collectionPath)
            .document(noteItemId)
            .update("content", content)
            .await()
    }

    override suspend fun updateAllNoteItemPositions(vararg noteItems: NoteItem) {
        val batch = firestore.batch()

        noteItems.forEach { noteItem ->
            val documentRef = firestore.collection(collectionPath)
                .document(noteItem.id)
            batch.update(documentRef, "position", noteItem.position)
        }

        batch.commit().await()
    }

    override suspend fun deleteNoteItemById(noteItemId: String) {
        firestore.collection(collectionPath)
            .document(noteItemId)
            .update("deleted", true)
            .await()
    }

    override suspend fun deletePermanentlyNoteItem(noteItemId: String) {
        firestore.collection(collectionPath)
            .document(noteItemId)
            .delete()
            .await()
    }

    override suspend fun tryRestoreNoteItemById(noteItemId: String) {
        firestore.collection(collectionPath)
            .document(noteItemId)
            .update("deleted", false)
            .await()
    }

    override suspend fun deleteAllNoteItems() {
        val batch = firestore.batch()

        firestore.collection(collectionPath)
            .get()
            .await()
            .documents
            .forEach { document ->
                val documentRef = firestore.collection(collectionPath)
                    .document(document.id)
                batch.delete(documentRef)
            }

        batch.commit().await()
    }
}