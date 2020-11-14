@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import android.content.Context
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

inline fun <T : Fragment> T.withArguments(vararg params: Pair<String, Any>): T {
    arguments = bundleOf(*params)
    return this
}

inline val Fragment.act: FragmentActivity
    get() = activity!!

@Suppress("UNCHECKED_CAST")
inline fun <T> Fragment.getArgument(key: String): T {
    return arguments!!.get(key) as T
}

inline fun Fragment.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline block: suspend CoroutineScope.() -> Unit,
): Job {
    return viewLifecycleOwner.lifecycleScope.launch(context, block = block)
}