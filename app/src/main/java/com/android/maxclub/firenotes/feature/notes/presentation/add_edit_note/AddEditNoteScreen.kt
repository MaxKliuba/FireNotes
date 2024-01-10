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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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

    val state by viewModel.uiState

    LaunchedEffect(key1 = true) {
        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is AddEditNoteUiAction.LaunchShareNoteIntent -> {
                    context.startActivity(action.intent)
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
        }
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
                    onLocalNoteItemsReorder = viewModel::reorderLocalNoteItems,
                    onApplyNoteItemsReorder = viewModel::applyNoteItemsReorder,
                    onNoteItemDelete = viewModel::deleteNoteItem,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}