package com.tech.maxclub.firenotes.feature.notes.domain.models

import androidx.compose.runtime.Stable

data class Note(
    val title: String,
    val timestamp: Long,
    val position: Long,
    val expanded: Boolean = true,
    @Stable val items: List<NoteItem> = emptyList(),
    val id: String = "",
) {
    fun toContentString(): String =
        "$title${items.joinToString(prefix = "\n\n", separator = "\n") { it.toContentString() }}"
}