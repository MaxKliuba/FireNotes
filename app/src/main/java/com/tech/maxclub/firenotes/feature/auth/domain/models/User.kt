package com.tech.maxclub.firenotes.feature.auth.domain.models

data class User(
    val id: String,
    val name: String,
    val photoUrl: String?,
    val isAnonymous: Boolean,
)