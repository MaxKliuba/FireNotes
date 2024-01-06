package com.android.maxclub.firenotes.feature.notes.data.mappers

import com.android.maxclub.firenotes.feature.notes.data.dto.NoteItemDto
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteItem

fun NoteItemDto.toNoteItem(noteItemId: String): NoteItem? =
    takeUnless { it.deleted }?.let {
        NoteItem(
            id = noteItemId,
            checked = checked,
            content = content,
            position = position,
        )
    }

fun NoteItem.toNoteDtoItem(): NoteItemDto =
    NoteItemDto(
        checked = checked,
        content = content,
        position = position,
    )