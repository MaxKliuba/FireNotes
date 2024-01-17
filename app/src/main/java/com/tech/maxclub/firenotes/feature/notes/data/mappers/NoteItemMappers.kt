package com.tech.maxclub.firenotes.feature.notes.data.mappers

import com.tech.maxclub.firenotes.feature.notes.data.dto.NoteItemDto
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteItem
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteItemType
import kotlin.reflect.KClass

fun NoteItemDto.toNoteItem(noteItemId: String): NoteItem? =
    takeUnless { it.deleted }?.let {
        when (type) {
            NoteItemType.TEXT -> NoteItem.Text(
                id = noteItemId,
                content = content,
                position = position,
            )

            NoteItemType.TODO -> NoteItem.ToDo(
                id = noteItemId,
                checked = checked,
                content = content,
                position = position,
            )
        }
    }

fun NoteItem.toNoteDtoItem(): NoteItemDto =
    NoteItemDto(
        type = this::class.toType(),
        checked = (this as? NoteItem.ToDo)?.checked ?: false,
        content = content,
        position = position,
    )

fun KClass<out NoteItem>.toType(): NoteItemType =
    when (this) {
        NoteItem.ToDo::class -> NoteItemType.TODO
        else -> NoteItemType.TEXT
    }