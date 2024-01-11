package com.android.maxclub.firenotes.feature.notes.presentation.add_edit_note

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.firenotes.R
import com.android.maxclub.firenotes.feature.notes.presentation.add_edit_note.components.NoteItemList
import com.android.maxclub.firenotes.feature.notes.presentation.add_edit_note.components.AddEditNoteTopAppBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddEditNoteScreen(
    onNavigateUp: () -> Unit,
    onDeleteNote: (String) -> Unit,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.uiState

    LaunchedEffect(key1 = true) {
        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is AddEditNoteUiAction.LaunchShareNoteIntent -> {
                    context.startActivity(action.intent)
                }

                is AddEditNoteUiAction.ShowNoteItemDeletedMessage -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.note_item_deleted_message),
                        actionLabel = context.getString(R.string.undo_button),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short,
                    ).let { result ->
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.tryRestoreNoteItem(action.noteItemId)
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AddEditNoteTopAppBar(
                note = state.note,
                onNoteTitleChange = viewModel::tryUpdateNoteTitle,
                onNavigateUp = onNavigateUp,
                onShareNote = viewModel::shareNote,
                onDeleteNote = { noteId ->
                    onDeleteNote(noteId)
                    onNavigateUp()
                }
            )
        },
        floatingActionButton = {
            state.note?.let {
                FloatingActionButton(onClick = viewModel::addNoteItem) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_item_button),
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.note?.let { note ->
                NoteItemList(
                    noteItems = note.items,
                    onNoteItemCheckedChange = viewModel::updateNoteItemChecked,
                    onNoteItemContentChange = viewModel::updateNoteItemContent,
                    onReorderLocalNoteItems = viewModel::reorderLocalNoteItems,
                    onApplyNoteItemsReorder = viewModel::applyNoteItemsReorder,
                    onDeleteNoteItem = viewModel::deleteNoteItem,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}