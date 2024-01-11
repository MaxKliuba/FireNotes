package com.android.maxclub.firenotes.feature.notes.presentation.notes

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
import com.android.maxclub.firenotes.feature.notes.presentation.notes.components.NoteList
import com.android.maxclub.firenotes.feature.notes.presentation.notes.components.NotesTopAppBar
import com.android.maxclub.firenotes.feature.notes.presentation.notes.components.UserProfileDialog

@Composable
fun NotesScreen(
    currentUser: User?,
    onSignOut: () -> Unit,
    onAddNote: () -> Unit,
    onEditNote: (String) -> Unit,
    onDeleteNote: (String) -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState

    if (state.isUserProfileDialogVisible) {
        UserProfileDialog(
            user = currentUser,
            notesCount = state.notes.size,
            onSignOut = onSignOut,
            onDismiss = viewModel::hideUserProfileDialog,
        )
    }

    Scaffold(
        topBar = {
            NotesTopAppBar(
                userPhotoUrl = currentUser?.photoUrl,
                onClickUserProfile = viewModel::showUserProfileDialog,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_note_button),
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

            NoteList(
                notes = state.notes,
                onReorderLocalNotes = viewModel::reorderLocalNotes,
                onApplyNotesReorder = viewModel::applyNotesReorder,
                onEditNote = onEditNote,
                onDeleteNote = onDeleteNote,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}