package com.tech.maxclub.firenotes.feature.notes.domain.models

data class NoteWithItemsCount(
    val title: String,
    val position: Long,
    val timestamp: Long,
    val itemsCount: Int,
    val id: String,
)