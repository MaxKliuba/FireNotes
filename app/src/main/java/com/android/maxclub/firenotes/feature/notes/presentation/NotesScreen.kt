package com.android.maxclub.firenotes.feature.notes.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.firenotes.feature.auth.domain.models.User
import com.android.maxclub.firenotes.feature.notes.presentation.components.NotesTopAppBar

@Composable
fun NotesScreen(
    currentUser: User?,
    onSignOut: () -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            NotesTopAppBar(
                userPhotoUrl = currentUser?.photoUrl,
                onClickUserPhoto = onSignOut,
                isDeleteIconVisible = true,
                onDelete = {}
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Text(text = currentUser?.name ?: "User")
        }
    }
}