package com.tech.maxclub.firenotes.feature.notes.presentation.add_edit_note.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragIndicator
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
fun NoteItemTextComponent(
    noteItem: NoteItem.Text,
    onContentChange: (String, String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    var contentValue by remember {
        mutableStateOf(TextFieldValue(noteItem.content))
    }

    LaunchedEffect(key1 = Unit) {
        if (noteItem.content.isEmpty()) {
            focusRequester.requestFocus()
        }
    }

    Row(modifier = modifier.background(color = MaterialTheme.colorScheme.background)) {
        ContentTextField(
            value = contentValue,
            onValueChange = {
                contentValue = it
                onContentChange(noteItem.id, it.text)
            },
            modifier = Modifier
                .padding(start = 18.dp, top = 12.dp, bottom = 12.dp)
                .heightIn(min = 24.dp)
                .weight(1f)
                .focusRequester(focusRequester)
        )

        IconButton(onClick = { onDelete(noteItem.id) }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.delete_note_item_button),
            )
        }

        Icon(
            imageVector = Icons.Default.DragIndicator,
            contentDescription = null,
            modifier = Modifier.padding(top = 12.dp, end = 16.dp)
        )
    }
}