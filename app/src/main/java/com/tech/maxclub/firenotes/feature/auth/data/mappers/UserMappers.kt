package com.tech.maxclub.firenotes.feature.auth.data.mappers

import com.tech.maxclub.firenotes.feature.auth.domain.models.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toUser(): User =
    User(
        id = this.uid,
        name = if (this.isAnonymous) "Anonymous" else this.displayName ?: "User",
        photoUrl = this.photoUrl?.toString(),
        isAnonymous = this.isAnonymous,
    )