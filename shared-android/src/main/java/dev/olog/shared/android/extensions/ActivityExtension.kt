package dev.olog.shared.android.extensions

import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun FragmentActivity.fragmentTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    supportFragmentManager
        .beginTransaction()
        .func()
        .commit()
}

fun FragmentManager.getTopFragment(): Fragment? {
    val topFragment = this.backStackEntryCount - 1
    if (topFragment > -1) {
        val tag = this.getBackStackEntryAt(topFragment).name
        return this.findFragmentByTag(tag)
    }
    return null
}

inline fun FragmentActivity.alertDialog(builder: MaterialAlertDialogBuilder.() -> MaterialAlertDialogBuilder) {
    MaterialAlertDialogBuilder(this)
        .builder()
        .show()
}

@Suppress("UnusedReceiverParameter")
fun<T : ViewBinding> ComponentActivity.viewBinding(
    factory: (View) -> T,
    onDestroy: (T) -> Unit = {},
) : ActivityViewBinding<T> {
    return ActivityViewBinding(factory, onDestroy)
}

class ActivityViewBinding<T :ViewBinding>(
    private val factory: (View) -> T,
    private val onDestroy: (T) -> Unit,
) : ReadOnlyProperty<ComponentActivity, T> {

    private var _binding: T? = null

    override fun getValue(thisRef: ComponentActivity, property: KProperty<*>): T {
        if (!thisRef.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            error("Can not get binding, activity state=${thisRef.lifecycle.currentState}")
        }
        _binding?.let {
            return it
        }

        val contentView = thisRef.findViewById<View>(android.R.id.content)
        return factory((contentView as ViewGroup).getChildAt(0)).also {
            _binding = it
            thisRef.lifecycle.addObserver(LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    onDestroy(it)
                    _binding = null
                }
            })
        }
    }
}