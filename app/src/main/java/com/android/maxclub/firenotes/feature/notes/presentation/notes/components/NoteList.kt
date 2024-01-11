package com.android.maxclub.firenotes.feature.notes.presentation.notes.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteWithItemsCount
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun NoteList(
    notes: List<NoteWithItemsCount>,
    onReorderLocalNotes: (fromIndex: Int, toIndex: Int) -> Unit,
    onApplyNotesReorder: () -> Unit,
    onEditNote: (noteId: String) -> Unit,
    onDeleteNote: (noteId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val state = rememberReorderableLazyListState(
        onMove = { from, to ->
            onReorderLocalNotes(from.index, to.index)
        },
        onDragEnd = { _, _ ->
            onApplyNotesReorder()
        }
    )

    LazyColumn(
        state = state.listState,
        contentPadding = PaddingValues(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 80.dp),
        modifier = modifier
            .reorderable(state)
            .detectReorderAfterLongPress(state)
    ) {
        items(
            items = notes,
            key = { it.id }
        ) { note ->
            ReorderableItem(
                reorderableState = state,
                key = note.id,
            ) { isDragging ->
                LaunchedEffect(isDragging) {
                    if (isDragging) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                }

                NoteComponent(
                    note = note,
                    isDragging = isDragging,
                    onEdit = onEditNote,
                    onDelete = onDeleteNote,
                )
            }
        }
    }
}