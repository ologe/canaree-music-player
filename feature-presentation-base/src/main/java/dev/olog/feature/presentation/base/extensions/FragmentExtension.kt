@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.feature.presentation.base.extensions

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

// TODO remove this??
inline fun <T : Fragment> T.withArguments(vararg params: Pair<String, Any>): T {
    arguments = bundleOf(*params)
    return this
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Fragment.getArgument(key: String): T {
    return requireArguments().get(key) as T
}

fun Fragment.launchWhenCreated(block: suspend CoroutineScope.() -> Unit): Job {
    return viewLifecycleOwner.lifecycleScope.launchWhenCreated(block)
}

fun Fragment.launchWhenStarted(block: suspend CoroutineScope.() -> Unit): Job {
    return viewLifecycleOwner.lifecycleScope.launchWhenStarted(block)
}

// TODO check usages
fun Fragment.launchWhenResumed(block: suspend CoroutineScope.() -> Unit): Job {
    return viewLifecycleOwner.lifecycleScope.launchWhenResumed(block)
}

