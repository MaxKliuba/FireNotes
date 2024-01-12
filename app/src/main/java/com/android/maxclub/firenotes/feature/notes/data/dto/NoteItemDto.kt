package com.android.maxclub.firenotes.feature.notes.data.dto

import androidx.annotation.Keep
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteItemType

@Keep
data class NoteItemDto(
    val type: NoteItemType = NoteItemType.TEXT,
    val checked: Boolean = false,
    val content: String = "",
    val position: Long = 0,
    val deleted: Boolean = false,
)