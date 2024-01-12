package com.android.maxclub.firenotes.feature.main.presentation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.maxclub.firenotes.R
import com.android.maxclub.firenotes.feature.auth.presentation.SignInScreen
import com.android.maxclub.firenotes.feature.notes.presentation.add_edit_note.AddEditNoteScreen
import com.android.maxclub.firenotes.feature.notes.presentation.notes.NotesScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MainScreenContainer(viewModel: MainViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()

    val isSigningIn by viewModel.isSigningIn
    val currentUser by viewModel.currentUser.collectAsState()
    val startDestination by remember {
        derivedStateOf {
            if (currentUser == null) Screen.SignIn.route else Screen.Notes.route
        }
    }

    val launcherEorSignInIntentResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { intent ->
                    viewModel.signInWithIntent(intent)
                }
            }
        }
    )

    LaunchedEffect(key1 = true) {
        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is MainUiAction.LaunchSignInIntent -> {
                    launcherEorSignInIntentResult.launch(
                        IntentSenderRequest.Builder(action.intentSender).build()
                    )
                }

                is MainUiAction.ShowAuthErrorMessage -> {
                    Toast.makeText(context, action.errorMessage, Toast.LENGTH_SHORT).show()
                }

                is MainUiAction.ShowNoteDeletedMessage -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.note_deleted_message),
                        actionLabel = context.getString(R.string.undo_button),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short,
                    ).let { result ->
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.tryRestoreNote(action.noteId)
                        }
                    }
                }

                is MainUiAction.ShowNotesErrorMessage -> {
                    Toast.makeText(context, action.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = Screen.SignIn.route) {
                SignInScreen(
                    isSigningIn = isSigningIn,
                    onSignIn = viewModel::beginSignIn,
                )
            }

            composable(route = Screen.Notes.route) {
                NotesScreen(
                    currentUser = currentUser,
                    onSignOut = viewModel::signOut,
                    onAddNote = { navController.navigate(Screen.AddEditNote.route) },
                    onEditNote = { noteId ->
                        navController.navigate("${Screen.AddEditNote.route}?${Screen.AddEditNote.ARG_NOTE_ID}=$noteId")
                    },
                    onDeleteNote = viewModel::deleteNote
                )
            }

            composable(
                route = Screen.AddEditNote.routeWithArgs,
                arguments = listOf(
                    navArgument(name = Screen.AddEditNote.ARG_NOTE_ID) {
                        type = NavType.StringType
                        defaultValue = Screen.AddEditNote.DEFAULT_NOTE_ID
                    }
                ),
            ) {
                AddEditNoteScreen(
                    onNavigateUp = navController::navigateUp,
                    onDeleteNote = viewModel::deleteNote,
                )
            }
        }
    }
}