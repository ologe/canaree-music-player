package dev.olog.core.coroutines

import android.os.Looper
import android.view.View
import androidx.core.view.doOnAttach
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.olog.core.extensions.awaitOnAttach
import dev.olog.core.extensions.findActivity
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private val JOB_KEY = "canaree.JOB_KEY".hashCode()

val View.viewScope: ViewScope
    get() {
        require(Looper.getMainLooper() == Looper.myLooper())

        var scope = getTag(JOB_KEY) as? ViewScope
        if (scope != null) {
            return scope
        }
        scope = ViewScope(this)

        doOnAttach {
            AttachableView(this)
        }

        setTag(JOB_KEY, scope)
        return scope
    }

class ViewScope(
    view: View
) : CoroutineScope {

    private val context: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate

    private val view = WeakReference(view)

    override val coroutineContext: CoroutineContext
        get() = context

    fun launchWhenAttached(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return launch(context) {
            view.get()?.awaitOnAttach() ?: return@launch
            block()
        }
    }
}

private class AttachableView(
    view: View
) {

    private val viewWeak = WeakReference(view)

    private val observer = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            viewWeak.get()?.viewScope?.cancel()
        }
    }

    init {
        val lifecycle = try {
            view.findFragment<Fragment>().viewLifecycleOwner.lifecycle
        } catch (ex: IllegalStateException) {
            // not child of a fragment
            view.findActivity().lifecycle
        }
        lifecycle.addObserver(observer)
    }

}