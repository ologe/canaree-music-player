package dev.olog.platform

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewBindingDelegate<T : ViewBinding>(
    private val fragment: Fragment,
    private val viewBindingFactory: (View) -> T,
) : ReadOnlyProperty<Fragment, T> {

    private var _binding: T? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (_binding == null) {
            _binding = viewBindingFactory(thisRef.requireView())
            fragment.viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    _binding = null
                }
            })
        }
        return _binding!!
    }
}

fun <T : ViewBinding> Fragment.viewBinding(
    viewBindingFactory: (View) -> T,
): ViewBindingDelegate<T> {
    return ViewBindingDelegate(this, viewBindingFactory)
}