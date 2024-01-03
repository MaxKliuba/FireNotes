package com.android.maxclub.firenotes.feature.auth.data

import com.android.maxclub.firenotes.feature.auth.domain.models.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toUser(): User =
    User(
        id = this.uid,
        name = this.displayName,
        photoUrl = this.photoUrl?.toString()
    )