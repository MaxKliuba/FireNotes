package com.tech.maxclub.firenotes.feature.main.presentation

sealed class Screen(val route: String) {
    data object SignIn : Screen("auth")

    data object Notes : Screen("notes")

    data object AddEditNote : Screen("add_edit_note") {
        const val ARG_NOTE_ID = "noteId" // optional
        const val DEFAULT_NOTE_ID = "null"

        val routeWithArgs: String
            get() = "$route?$ARG_NOTE_ID={$ARG_NOTE_ID}"
    }
}