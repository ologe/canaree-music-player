package dev.olog.core.extensions

import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.core.view.doOnAttach
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun View.awaitOnAttach() = suspendCancellableCoroutine<Unit> { continuation ->
    doOnAttach {
        continuation.resume(Unit)
    }
}

/**
 * Find a [FragmentActivity] associated with a [View].
 *
 * This method will locate the [FragmentActivity] associated with this view.
 *
 * Calling this on a View that does not have a FragmentActivity set will result in an
 * [IllegalStateException]
 */
fun View.findActivity(): FragmentActivity {
    var context: Context = context
    while (context is ContextWrapper) {
        if (context is FragmentActivity) {
            return context
        }
        context = context.baseContext
    }
    throw IllegalStateException("View $this does not have a FragmentActivity set")
}

fun View.setMatchParent() {
    val params = layoutParams ?: LayoutParams(MATCH_PARENT, MATCH_PARENT)
    params.width = MATCH_PARENT
    params.height = MATCH_PARENT
    layoutParams = params
}