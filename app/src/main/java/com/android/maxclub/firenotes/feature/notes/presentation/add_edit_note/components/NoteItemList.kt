package com.android.maxclub.firenotes.feature.notes.presentation.add_edit_note.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteItem
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItemList(
    noteItems: List<NoteItem>,
    onNoteItemCheckedChange: (noteItemId: String, isChecked: Boolean) -> Unit,
    onNoteItemContentChange: (noteItemId: String, content: String) -> Unit,
    onLocalNoteItemsReorder: (fromIndex: Int, toIndex: Int) -> Unit,
    onApplyNoteItemsReorder: () -> Unit,
    onNoteItemDelete: (noteItemId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val state = rememberReorderableLazyListState(
        onMove = { from, to ->
            onLocalNoteItemsReorder(from.index, to.index)
        },
        onDragEnd = { _, _ ->
            onApplyNoteItemsReorder()
        }
    )

    LazyColumn(
        state = state.listState,
        contentPadding = PaddingValues(bottom = 72.dp),
        modifier = modifier
            .reorderable(state)
            .detectReorderAfterLongPress(state)
    ) {
        items(
            items = noteItems,
            key = { it.id }
        ) { noteItem ->
            ReorderableItem(
                reorderableState = state,
                key = noteItem.id,
            ) { isDragging ->
                LaunchedEffect(isDragging) {
                    if (isDragging) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                }

                NoteItemComponent(
                    noteItem = noteItem,
                    onCheckedChange = onNoteItemCheckedChange,
                    onContentChange = onNoteItemContentChange,
                    onDelete = onNoteItemDelete,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}