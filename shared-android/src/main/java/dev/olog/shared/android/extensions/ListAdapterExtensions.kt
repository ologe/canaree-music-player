package dev.olog.shared.android.extensions

import androidx.recyclerview.widget.ListAdapter
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun <T> ListAdapter<T, *>.suspendSubmitList(
    list: List<T>
) = suspendCancellableCoroutine<Unit> {
    submitList(list) {
        it.resume(Unit)
    }
}