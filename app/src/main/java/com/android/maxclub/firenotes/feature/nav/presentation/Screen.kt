package com.android.maxclub.firenotes.feature.nav.presentation

sealed class Screen(val route: String) {
    data object SignIn : Screen("auth")
    data object Notes : Screen("notes")
}