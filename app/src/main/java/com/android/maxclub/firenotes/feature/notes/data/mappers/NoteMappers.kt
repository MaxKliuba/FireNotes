package com.android.maxclub.firenotes.feature.notes.data.mappers

import com.android.maxclub.firenotes.feature.notes.data.dto.NoteDto
import com.android.maxclub.firenotes.feature.notes.domain.models.Note
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteWithItemsCount

fun NoteDto.toNoteWithItemsCount(noteId: String, itemsCount: Int): NoteWithItemsCount? =
    takeUnless { it.deleted }?.let {
        NoteWithItemsCount(
            title = title,
            position = position,
            itemsCount = itemsCount,
            id = noteId,
        )
    }

fun NoteDto.toNote(noteId: String): Note? =
    takeUnless { it.deleted }?.let {
        Note(
            title = title,
            timestamp = timestamp,
            position = position,
            items = emptyList(),
            id = noteId,
        )
    }

fun Note.toNoteDto(): NoteDto =
    NoteDto(
        title = title,
        timestamp = timestamp,
        position = position,
    )