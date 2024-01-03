package com.android.maxclub.firenotes.feature.nav.presentation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.maxclub.firenotes.core.Screen
import com.android.maxclub.firenotes.feature.auth.presentation.SignInScreen
import com.android.maxclub.firenotes.feature.notes.presentation.NotesScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NavHostScreen(
    viewModel: NavHostViewModel = hiltViewModel()
) {
    val context = LocalContext.current

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
                is NavHostUiAction.LaunchSignInIntent -> {
                    launcherEorSignInIntentResult.launch(
                        IntentSenderRequest.Builder(action.intentSender).build()
                    )
                }

                is NavHostUiAction.ShowAuthErrorMessage -> {
                    Toast.makeText(context, action.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(route = Screen.SignIn.route) {
            SignInScreen(onSignIn = viewModel::beginSignIn)
        }

        composable(route = Screen.Notes.route) {
            NotesScreen(
                currentUser = currentUser,
                onSignOut = viewModel::signOut
            )
        }
    }
}