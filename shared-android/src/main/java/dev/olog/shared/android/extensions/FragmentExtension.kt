@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

inline fun <T : Fragment> T.withArguments(vararg params: Pair<String, Any>): T {
    arguments = bundleOf(*params)
    return this
}

inline fun Fragment.dip(value: Int): Int = requireContext().dip(value)
inline fun Fragment.dip(value: Float): Int = requireContext().dip(value)
inline fun Fragment.dimen(@DimenRes resId: Int): Int = requireContext().dimen(resId)

inline fun Fragment.toast(@StringRes resId: Int) = requireContext().toast(resId)
inline fun Fragment.toast(message: CharSequence) = requireContext().toast(message)

@Suppress("UNCHECKED_CAST", "ObjectPropertyName")
fun <T : Any> Fragment.argument(key: String): Lazy<T> {
    return object : Lazy<T> {
        private var _value: T? = null

        override val value: T
            get() {
                if (_value == null) {
                    _value = requireArguments().get(key) as T
                }
                return _value!!
            }

        override fun isInitialized(): Boolean = _value != null
    }
}

inline fun <T : Any> Flow<T>.collectOnViewLifecycle(
    fragment: Fragment,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    collect: FlowCollector<T>,
) {
    return collectOnLifecycle(
        owner = fragment.viewLifecycleOwner,
        minActiveState = minActiveState,
        collect = collect
    )
}

fun Fragment.launchWhenStarted(block: suspend CoroutineScope.() -> Unit) {
    viewLifecycleOwner.lifecycleScope.launchWhenStarted(block)
}

fun Fragment.launchWhenResumed(block: suspend CoroutineScope.() -> Unit) {
    viewLifecycleOwner.lifecycleScope.launchWhenResumed(block)
}