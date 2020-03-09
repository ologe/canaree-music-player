@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import android.content.Context
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import dev.olog.shared.android.theme.ThemeManager
import dev.olog.shared.android.theme.themeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

inline fun <T : Fragment> T.withArguments(vararg params: Pair<String, Any>): T {
    arguments = bundleOf(*params)
    return this
}

inline val Fragment.ctx: Context
    get() = requireActivity()

inline val Fragment.act: FragmentActivity
    get() = requireActivity()

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

fun Fragment.launchWhenResumed(block: suspend CoroutineScope.() -> Unit): Job {
    return viewLifecycleOwner.lifecycleScope.launchWhenResumed(block)
}

val Fragment.themeManager: ThemeManager
    get() = requireContext().themeManager