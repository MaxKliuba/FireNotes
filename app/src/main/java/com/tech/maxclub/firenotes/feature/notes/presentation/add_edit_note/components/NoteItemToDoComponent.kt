package com.tech.maxclub.firenotes.feature.notes.presentation.add_edit_note.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.tech.maxclub.firenotes.R
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteItem

@Composable
fun NoteItemToDoComponent(
    noteItem: NoteItem.ToDo,
    onCheckedChange: (String, Boolean) -> Unit,
    onContentChange: (String, String) -> Boolean,
    onAddToDoItem: () -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    var checked by remember {
        mutableStateOf(noteItem.checked)
    }
    var contentValue by remember {
        mutableStateOf(TextFieldValue(noteItem.content))
    }

    LaunchedEffect(key1 = Unit) {
        if (noteItem.content.isEmpty()) {
            focusRequester.requestFocus()
        }
    }

    Row(modifier = modifier.background(color = MaterialTheme.colorScheme.background)) {
        Checkbox(
            checked = checked,
            onCheckedChange = {
                checked = it
                onCheckedChange(noteItem.id, it)
            },
            modifier = Modifier
                .padding(start = 8.dp, top = 2.dp, bottom = 2.dp)
                .size(40.dp)
        )

        CheckedContentTextField(
            checked = checked,
            value = contentValue,
            onValueChange = {
                if (onContentChange(noteItem.id, it.text)) {
                    contentValue = it
                }
            },
            onNextAction = onAddToDoItem,
            modifier = Modifier
                .padding(start = 4.dp, top = 10.dp, bottom = 10.dp)
                .heightIn(min = 24.dp)
                .weight(1f)
                .focusRequester(focusRequester)
        )

        IconButton(
            onClick = { onDelete(noteItem.id) },
            modifier = Modifier
                .padding(2.dp)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.delete_note_item_button),
            )
        }

        Icon(
            imageVector = Icons.Default.DragIndicator,
            contentDescription = null,
            modifier = Modifier.padding(top = 10.dp, end = 16.dp)
        )
    }
}