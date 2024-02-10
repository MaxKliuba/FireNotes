package com.tech.maxclub.firenotes.feature.notes.domain.models

import androidx.compose.runtime.Stable

data class NoteWithItemsPreview(
    val title: String,
    val timestamp: Long,
    val position: Long,
    val expanded: Boolean = false,
    val itemsCount: Int,
    @Stable val previewItems: List<NoteItem>,
    val id: String,
)