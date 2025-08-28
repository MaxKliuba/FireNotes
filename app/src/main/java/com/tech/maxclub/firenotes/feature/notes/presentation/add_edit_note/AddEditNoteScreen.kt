package com.tech.maxclub.firenotes.feature.notes.presentation.add_edit_note

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.tech.maxclub.firenotes.R
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteItemType
import com.tech.maxclub.firenotes.feature.notes.presentation.add_edit_note.components.AddEditNoteTopAppBar
import com.tech.maxclub.firenotes.feature.notes.presentation.add_edit_note.components.AddNoteItemFab
import com.tech.maxclub.firenotes.feature.notes.presentation.add_edit_note.components.NoteItemList
import com.tech.maxclub.firenotes.ui.components.BaseScaffold
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

                is AddEditNoteUiAction.ShowNoteErrorMessage -> {
                    Toast.makeText(context, action.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    BaseScaffold(
        topBar = {
            AddEditNoteTopAppBar(
                note = state.note,
                onNoteTitleChange = viewModel::tryUpdateNoteTitle,
                onNavigateUp = onNavigateUp,
                onShareNote = viewModel::shareNote,
                onDeleteNote = { noteId ->
                    onDeleteNote(noteId)
                    onNavigateUp()
                },
                onAddTextItem = if (state.note?.items?.isEmpty() == true) {
                    { viewModel.addNoteItem(NoteItemType.TEXT) }
                } else {
                    null
                }
            )
        },
        floatingActionButton = {
            state.note?.let {
                AddNoteItemFab(
                    isOpen = state.isFabOpen,
                    onChangeState = viewModel::setFabState,
                    onAddNoteItem = viewModel::addNoteItem,
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    actionColor = MaterialTheme.colorScheme.primary,
                    dismissActionContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    snackbarData = data,
                )
            }
        },
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
                    onNoteItemContentChange = viewModel::tryUpdateNoteItemContent,
                    onReorderLocalNoteItems = viewModel::reorderLocalNoteItems,
                    onApplyNoteItemsReorder = viewModel::applyNoteItemsReorder,
                    onAddNoteItem = viewModel::addNoteItem,
                    onDeleteNoteItem = viewModel::deleteNoteItem,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}