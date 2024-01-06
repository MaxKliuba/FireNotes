package com.android.maxclub.firenotes.feature.notes.domain.usecases

import com.android.maxclub.firenotes.feature.notes.domain.models.NoteWithItemsCount
import com.android.maxclub.firenotes.feature.notes.domain.repositories.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(): Flow<List<NoteWithItemsCount>> =
        noteRepository.getNotes().map { notes ->
            notes.sortedBy { it.position }
        }
}