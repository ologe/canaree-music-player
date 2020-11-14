@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import androidx.annotation.DimenRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.properties.ReadOnlyProperty

inline fun <T : Fragment> T.withArguments(vararg params: Pair<String, Any>): T {
    arguments = bundleOf(*params)
    return this
}

inline fun Fragment.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline block: suspend CoroutineScope.() -> Unit,
): Job {
    return viewLifecycleOwner.lifecycleScope.launch(context, block = block)
}

inline fun Fragment.dip(value: Int): Int = requireContext().dip(value)
inline fun Fragment.dipf(value: Int): Float = requireContext().dipf(value)
inline fun Fragment.dimen(@DimenRes resource: Int): Int = requireContext().dimen(resource)

// can't be named arguments because it clashes with [Fragment.arguments]
inline fun <reified T : Any?> Fragment.argument(
    key: String,
    crossinline initializer: (T) -> T = { it }
): ReadOnlyProperty<Any?, T> {
    return argument<T, T>(key, initializer)
}

@JvmName("argument2")
inline fun <reified T : Any?, R : Any?> Fragment.argument(
    key: String,
    crossinline initializer: (T) -> R
): ReadOnlyProperty<Any?, R> {
    return ReadOnlyProperty { _, _ ->
        val argument = requireArguments().get(key) as T
        initializer(argument)
    }
}