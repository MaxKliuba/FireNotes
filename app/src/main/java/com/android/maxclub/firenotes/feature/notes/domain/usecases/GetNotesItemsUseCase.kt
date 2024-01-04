package com.android.maxclub.firenotes.feature.notes.domain.usecases

import com.android.maxclub.firenotes.feature.notes.domain.models.NoteItem
import com.android.maxclub.firenotes.feature.notes.domain.repositories.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNotesItemsUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(): Flow<List<NoteItem>> =
        noteRepository.getNoteItems().map { noteItems ->
            noteItems.sortedBy { it.position }
        }
}