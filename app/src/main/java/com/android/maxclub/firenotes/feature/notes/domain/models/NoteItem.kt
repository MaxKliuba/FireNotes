package com.android.maxclub.firenotes.feature.notes.domain.models

data class NoteItem(
    val checked: Boolean,
    val content: String,
    val position: Long,
    val id: String = "",
)