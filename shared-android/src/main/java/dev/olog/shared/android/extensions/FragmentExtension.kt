@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import android.content.Context
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <T: Fragment> T.withArguments(vararg params: Pair<String, Any>) : T {
    arguments = bundleOf(*params)
    return this
}

@Suppress("UnusedReceiverParameter")
fun <T> Fragment.requireArgument(key: String): ReadOnlyProperty<Fragment, T> {
    return ReadOnlyProperty { thisRef, _ ->
        thisRef.getArgument(key)
    }
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


@Suppress("UnusedReceiverParameter")
fun<T : ViewBinding> Fragment.viewBinding(
    factory: (View) -> T,
    onDestroy: (T) -> Unit = {},
) : FragmentViewBinding<T> {
    return FragmentViewBinding(factory, onDestroy)
}

class FragmentViewBinding<T :ViewBinding>(
    private val factory: (View) -> T,
    private val onDestroy: (T) -> Unit,
) : ReadOnlyProperty<Fragment, T> {

    private var _binding: T? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (!thisRef.viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            error("Can not get binding, fragment state=${thisRef.viewLifecycleOwner.lifecycle.currentState}")
        }
        _binding?.let {
            return it
        }

        return factory(thisRef.requireView()).also {
            _binding = it
            thisRef.viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    onDestroy(it)
                    _binding = null
                }
            })
        }
    }
}