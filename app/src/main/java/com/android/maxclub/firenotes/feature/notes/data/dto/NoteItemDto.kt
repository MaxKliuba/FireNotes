package com.android.maxclub.firenotes.feature.notes.data.dto

data class NoteItemDto(
    val checked: Boolean = false,
    val content: String = "",
    val position: Long = 0L,
    val deleted: Boolean = false,
)