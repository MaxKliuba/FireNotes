package com.tech.maxclub.firenotes.feature.notes.domain.usecases

import com.tech.maxclub.firenotes.feature.notes.data.mappers.toNoteWithItemsPreview
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteWithItemsPreview
import com.tech.maxclub.firenotes.feature.notes.domain.repositories.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(): Flow<List<NoteWithItemsPreview>> =
        noteRepository.getNotes().map { notes ->
            notes.sortedByDescending { it.position }
                .map { note ->
                    note.copy(items = note.items.sortedBy { it.position })
                        .toNoteWithItemsPreview(previewSize = 10)
                }
        }
}