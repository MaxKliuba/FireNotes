package com.android.maxclub.firenotes.feature.notes.domain.models

import androidx.compose.runtime.Stable

data class Note(
    val title: String,
    val timestamp: Long,
    val position: Long,
    @Stable val items: List<NoteItem> = emptyList(),
    val id: String = "",
)