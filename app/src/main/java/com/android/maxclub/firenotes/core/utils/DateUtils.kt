package com.android.maxclub.firenotes.core.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.android.maxclub.firenotes.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun formatDate(date: Date): String =
    formatDate(date = date, context = LocalContext.current)

fun formatDate(
    date: Date,
    context: Context,
): String {
    val pattern = context.getString(R.string.pattern)
    val locale = Locale(context.getString(R.string.language), context.getString(R.string.country))

    return SimpleDateFormat(pattern, locale).format(date)
}