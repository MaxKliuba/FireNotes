package com.tech.maxclub.firenotes.feature.notes.data.mappers

import com.tech.maxclub.firenotes.feature.notes.data.dto.NoteDto
import com.tech.maxclub.firenotes.feature.notes.domain.models.Note
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteItem
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteWithItemsPreview

fun NoteDto.toNote(noteId: String, items: List<NoteItem> = emptyList()): Note? =
    takeUnless { it.deleted }?.let {
        Note(
            title = title,
            timestamp = timestamp,
            position = position,
            expanded = expanded,
            items = items,
            id = noteId,
        )
    }

fun Note.toNoteDto(): NoteDto =
    NoteDto(
        title = title,
        timestamp = timestamp,
        position = position,
        expanded = expanded,
    )

fun Note.toNoteWithItemsPreview(previewSize: Int): NoteWithItemsPreview =
    NoteWithItemsPreview(
        title = title,
        timestamp = timestamp,
        position = position,
        expanded = expanded,
        itemsCount = items.size,
        previewItems = items.take(previewSize),
        id = id,
    )