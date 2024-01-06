package com.android.maxclub.firenotes.feature.notes.presentation.add_edit_note.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.android.maxclub.firenotes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteTopAppBar(
    noteId: String?,
    noteTitle: String?,
    onNoteTitleChange: (String, String) -> Unit,
    onNavigateUp: () -> Unit,
    onDeleteNote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            noteId?.let {
                var noteTitleValue by remember {
                    mutableStateOf(TextFieldValue(noteTitle ?: ""))
                }

                NoteTitleTextField(
                    value = noteTitleValue,
                    onValueChange = {
                        noteTitleValue = it
                        onNoteTitleChange(noteId, noteTitleValue.text)
                    },
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                )
            }
        },
        actions = {
            noteId?.let {
                IconButton(onClick = { onDeleteNote(noteId) }) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = stringResource(R.string.delete_note_button),
                    )
                }
            }
        },
        modifier = modifier
    )
}