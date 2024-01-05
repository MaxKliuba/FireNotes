package com.android.maxclub.firenotes.feature.notes.presentation

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.firenotes.R
import com.android.maxclub.firenotes.feature.auth.domain.models.User
import com.android.maxclub.firenotes.feature.notes.presentation.components.NoteItemList
import com.android.maxclub.firenotes.feature.notes.presentation.components.AddEditNoteTopAppBar

@Composable
fun AddEditNoteScreen(
    currentUser: User?,
    onSignOut: () -> Unit,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val state by viewModel.uiState

    Scaffold(
        topBar = {
            AddEditNoteTopAppBar(
                userPhotoUrl = currentUser?.photoUrl,
                onClickUserPhoto = onSignOut,
                isDeleteIconVisible = state.noteItems.isNotEmpty(),
                onDelete = viewModel::deleteAllNoteItems
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::addNoteItem) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_item_text),
                )
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

            NoteItemList(
                noteItems = state.noteItems,
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