package com.tech.maxclub.firenotes.feature.notes.data.mappers

import com.tech.maxclub.firenotes.feature.notes.data.dto.NoteDto
import com.tech.maxclub.firenotes.feature.notes.domain.models.Note
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteWithItemsCount

fun NoteDto.toNoteWithItemsCount(noteId: String, itemsCount: Int): NoteWithItemsCount? =
    takeUnless { it.deleted }?.let {
        NoteWithItemsCount(
            title = title,
            position = position,
            timestamp = timestamp,
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