package com.tech.maxclub.firenotes.feature.notes.presentation.add_edit_note.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tech.maxclub.firenotes.R
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteItem
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteItemType
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItemList(
    noteItems: List<NoteItem>,
    onNoteItemCheckedChange: (noteItemId: String, isChecked: Boolean) -> Unit,
    onNoteItemContentChange: (noteItemId: String, content: String) -> Boolean,
    onReorderLocalNoteItems: (fromIndex: Int, toIndex: Int) -> Unit,
    onApplyNoteItemsReorder: () -> Unit,
    onAddNoteItem: (NoteItemType) -> Unit,
    onDeleteNoteItem: (noteItemId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val state = rememberReorderableLazyListState(
        onMove = { from, to ->
            onReorderLocalNoteItems(from.index, to.index)
        },
        onDragEnd = { _, _ ->
            onApplyNoteItemsReorder()
        }
    )

    Box(modifier = modifier) {
        LazyColumn(
            state = state.listState,
            contentPadding = PaddingValues(bottom = 76.dp),
            modifier = Modifier
                .fillMaxSize()
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

                    when (noteItem) {
                        is NoteItem.Text -> NoteItemTextComponent(
                            noteItem = noteItem,
                            onContentChange = onNoteItemContentChange,
                            onDelete = onDeleteNoteItem,
                            modifier = Modifier.animateItemPlacement()
                        )

                        is NoteItem.ToDo -> NoteItemToDoComponent(
                            noteItem = noteItem,
                            onCheckedChange = onNoteItemCheckedChange,
                            onContentChange = onNoteItemContentChange,
                            onAddToDoItem = { onAddNoteItem(NoteItemType.TODO) },
                            onDelete = onDeleteNoteItem,
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }
        }

        if (noteItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onAddNoteItem(NoteItemType.TEXT) }
            ) {
                Text(
                    text = stringResource(R.string.empty_note_placeholder),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 18.dp, top = 10.dp)
                )
            }
        }
    }
}