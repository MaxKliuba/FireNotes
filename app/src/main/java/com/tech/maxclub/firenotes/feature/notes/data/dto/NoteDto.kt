package com.tech.maxclub.firenotes.feature.notes.data.dto

import androidx.annotation.Keep

@Keep
data class NoteDto(
    val title: String = "",
    val timestamp: Long = 0,
    val position: Long = 0,
    val deleted: Boolean = false,
)