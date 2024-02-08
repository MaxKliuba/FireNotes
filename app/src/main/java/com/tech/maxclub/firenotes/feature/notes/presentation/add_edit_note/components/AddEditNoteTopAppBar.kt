package com.tech.maxclub.firenotes.feature.notes.presentation.add_edit_note.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.tech.maxclub.firenotes.R
import com.tech.maxclub.firenotes.feature.notes.domain.models.Note

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteTopAppBar(
    note: Note?,
    onNoteTitleChange: (String, String) -> Boolean,
    onNavigateUp: () -> Unit,
    onShareNote: (Note) -> Unit,
    onDeleteNote: (String) -> Unit,
    onAddTextItem: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    TopAppBar(
        title = {
            note?.let {
                var noteTitleValue by remember {
                    mutableStateOf(TextFieldValue(note.title))
                }

                LaunchedEffect(key1 = Unit) {
                    if (note.title.isEmpty() && note.items.isEmpty()) {
                        focusRequester.requestFocus()
                    }
                }

                Column {
                    NoteTitleTextField(
                        value = noteTitleValue,
                        onValueChange = {
                            if (onNoteTitleChange(note.id, it.text)) {
                                noteTitleValue = it
                            }
                        },
                        onNextAction = onAddTextItem,
                        modifier = Modifier.focusRequester(focusRequester)
                    )
                }
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
            note?.let {
                IconButton(onClick = { onShareNote(note) }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share_note_button),
                    )
                }

                IconButton(onClick = { onDeleteNote(note.id) }) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = stringResource(R.string.delete_note_button),
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        modifier = modifier
    )
}