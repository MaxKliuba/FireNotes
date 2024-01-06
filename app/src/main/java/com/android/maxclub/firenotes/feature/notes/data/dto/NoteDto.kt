package com.android.maxclub.firenotes.feature.notes.data.dto

data class NoteDto(
    val title: String = "",
    val position: Long = 0,
    val deleted: Boolean = false,
)