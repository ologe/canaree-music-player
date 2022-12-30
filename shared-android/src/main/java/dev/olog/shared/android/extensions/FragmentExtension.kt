@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import android.content.Context
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope

inline fun <T: Fragment> T.withArguments(vararg params: Pair<String, Any>) : T {
    arguments = bundleOf(*params)
    return this
}

inline val Fragment.ctx : Context
    get() = context!!

inline val Fragment.act : FragmentActivity
    get() = activity!!

@Suppress("UNCHECKED_CAST")
inline fun <T> Fragment.getArgument(key: String): T {
    return arguments!!.get(key) as T
}

val Fragment.viewLifecycleScope: LifecycleCoroutineScope
    get() = viewLifecycleOwner.lifecycleScope