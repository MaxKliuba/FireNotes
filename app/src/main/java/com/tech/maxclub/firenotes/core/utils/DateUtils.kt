package com.tech.maxclub.firenotes.core.utils

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.tech.maxclub.firenotes.R
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
    val pattern = if (DateFormat.is24HourFormat(context)) {
        context.getString(R.string.datetime_pattern_24h)
    } else {
        context.getString(R.string.datetime_pattern_12h)
    }
    val locale = Locale(context.getString(R.string.language), context.getString(R.string.country))

    return SimpleDateFormat(pattern, locale).format(date).replaceFirstChar { it.uppercaseChar() }
}