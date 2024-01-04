package com.android.maxclub.firenotes.feature.notes.domain.models

data class NoteItem(
    val isChecked: Boolean,
    val content: String,
    val position: Int,
    val id: String = "",
)