package com.tech.maxclub.firenotes.feature.notes.presentation.add_edit_note.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tech.maxclub.firenotes.R
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteItemType

@Composable
fun AddNoteItemFab(
    isOpen: Boolean,
    onChangeState: (Boolean) -> Unit,
    onAddNoteItem: (NoteItemType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (isOpen) {
            SmallFloatingActionButton(onClick = { onAddNoteItem(NoteItemType.TODO) }) {
                Icon(
                    imageVector = Icons.Default.Checklist,
                    contentDescription = stringResource(R.string.add_todo_button)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SmallFloatingActionButton(onClick = { onAddNoteItem(NoteItemType.TEXT) }) {
                Icon(
                    imageVector = Icons.Default.TextFields,
                    contentDescription = stringResource(R.string.add_text_button)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        FloatingActionButton(onClick = { onChangeState(!isOpen) }) {
            if (isOpen) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_button)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_item_button)
                )
            }
        }
    }
}