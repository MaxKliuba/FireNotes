package com.android.maxclub.firenotes.feature.notes.domain.models

data class NoteWithItemsCount(
    val title: String,
    val position: Long,
    val itemsCount: Int,
    val id: String,
)