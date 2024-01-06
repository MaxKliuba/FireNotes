package com.android.maxclub.firenotes.feature.notes.presentation.add_edit_note.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.android.maxclub.firenotes.R
import com.android.maxclub.firenotes.feature.notes.domain.models.NoteItem

@Composable
fun NoteItemComponent(
    noteItem: NoteItem,
    onCheckedChange: (String, Boolean) -> Unit,
    onContentChange: (String, String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var checked by remember {
        mutableStateOf(noteItem.checked)
    }
    var contentValue by remember {
        mutableStateOf(TextFieldValue(noteItem.content))
    }

    Row(modifier = modifier.background(color = MaterialTheme.colorScheme.surface)) {
        Spacer(modifier = Modifier.width(18.dp))

        Icon(
            imageVector = Icons.Default.DragIndicator,
            contentDescription = null,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Checkbox(
            checked = checked,
            onCheckedChange = {
                checked = it
                onCheckedChange(noteItem.id, it)
            }
        )

        NoteItemContentTextField(
            checked = checked,
            value = contentValue,
            onValueChange = {
                contentValue = it
                onContentChange(noteItem.id, it.text)
            },
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 4.dp)
                .heightIn(min = 24.dp)
                .weight(1f)
        )

        IconButton(
            onClick = { onDelete(noteItem.id) },
            modifier = Modifier.padding(end = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.delete_note_item_button),
            )
        }
    }
}