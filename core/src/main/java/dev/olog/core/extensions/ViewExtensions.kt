package dev.olog.core.extensions

import android.content.Context
import android.content.ContextWrapper
import android.view.View
import androidx.core.view.doOnAttach
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun View.awaitOnAttach() = suspendCancellableCoroutine<Unit> { continuation ->
    if (isAttachedToWindow) {
        continuation.resume(Unit)
        return@suspendCancellableCoroutine
    }
    val listener = object : View.OnAttachStateChangeListener {

        override fun onViewAttachedToWindow(v: View) {
            removeOnAttachStateChangeListener(this)
            continuation.resume(Unit)
        }

        override fun onViewDetachedFromWindow(v: View) {}
    }
    addOnAttachStateChangeListener(listener)
    continuation.invokeOnCancellation { removeOnAttachStateChangeListener(listener) }
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