package com.android.maxclub.firenotes.feature.auth.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,
    val name: String?,
    val photoUrl: String?,
) : Parcelable