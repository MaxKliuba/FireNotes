package com.android.maxclub.firenotes.core.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T, V> CoroutineScope.debounce(
    timeoutMillis: Long = 300L,
    block: suspend (T, V) -> Unit
): (T, V) -> Unit {
    var job: Job? = null

    return { param1: T, param2: V ->
        job?.cancel() // Cancel the previous debounce job

        job = launch {
            delay(timeoutMillis)
            block(param1, param2)
        }
    }
}