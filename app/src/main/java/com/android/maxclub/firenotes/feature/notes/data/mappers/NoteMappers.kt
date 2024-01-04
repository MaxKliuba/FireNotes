package com.android.maxclub.firenotes.feature.notes.data.mappers

import com.android.maxclub.firenotes.feature.notes.data.dto.NoteItemDto
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteItem

fun NoteItemDto.toNoteItem(noteItemId: String): NoteItem =
    NoteItem(
        id = noteItemId,
        isChecked = isChecked,
        content = content,
        position = position,
    )

fun NoteItem.toNoteDtoItem(): NoteItemDto =
    NoteItemDto(
        isChecked = isChecked,
        content = content,
        position = position,
    )