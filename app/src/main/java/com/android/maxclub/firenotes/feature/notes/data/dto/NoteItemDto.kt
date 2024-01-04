package com.android.maxclub.firenotes.feature.notes.data.dto

data class NoteItemDto(
    val isChecked: Boolean = false,
    val content: String = "",
    val position: Int = 0,
    val isDeleted: Boolean = false,
)