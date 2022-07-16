package dev.olog.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope

@Composable
fun LaunchedBooleanEffect(
    predicate: Boolean,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedEffect(predicate) {
        if (predicate) {
            block()
        }
    }
}

@Composable
fun LaunchedBooleanEffect(
    predicate1: Boolean,
    predicate2: Boolean,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedEffect(predicate1, predicate2) {
        if (predicate1 || predicate2) {
            block()
        }
    }
}