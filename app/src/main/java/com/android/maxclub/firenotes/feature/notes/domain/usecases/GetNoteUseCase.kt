package com.android.maxclub.firenotes.feature.notes.domain.usecases

import com.android.maxclub.firenotes.feature.notes.domain.models.Note
import com.android.maxclub.firenotes.feature.notes.domain.repositories.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(noteId: String): Flow<Note> =
        noteRepository.getNoteById(noteId).map { note ->
            note.copy(items = note.items.sortedBy { it.position })
        }
}