@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import android.content.Context
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

inline fun <T: Fragment> T.withArguments(vararg params: Pair<String, Any>) : T {
    arguments = bundleOf(*params)
    return this
}

inline val Fragment.ctx : Context
    get() = context!!

inline val Fragment.act : FragmentActivity
    get() = activity!!

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