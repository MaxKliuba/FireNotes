package com.android.maxclub.firenotes.core

sealed class Screen(val route: String) {
    data object SignIn : Screen("auth")
    data object Notes : Screen("notes")
}