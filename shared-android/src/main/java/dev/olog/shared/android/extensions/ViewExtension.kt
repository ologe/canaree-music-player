@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.annotation.Px
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import dev.olog.core.coroutines.autoDisposeJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job


fun View.toggleVisibility(visible: Boolean) {
    if (visible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.INVISIBLE
    }
}

inline fun View.toggleSelected() {
    this.isSelected = !this.isSelected
}

inline fun ViewGroup.forEachRecursively(action: (view: View) -> Unit) {
    forEach {
        if (it is ViewGroup) {
            it.forEach(action)
        } else {
            action(it)
        }
    }
}

fun View.setHeight(@Px heightPx: Int) {
    val params = this.layoutParams
    params.height = heightPx
    layoutParams = params
}

fun View.setWidth(@Px heightPx: Int) {
    val params = this.layoutParams
    params.width = heightPx
    layoutParams = params
}

fun View.setMargin(
    @Px left: Int = marginLeft,
    @Px top: Int = marginTop,
    @Px right: Int = marginRight,
    @Px bottom: Int = marginBottom
) {
    val params = this.layoutParams as? ViewGroup.MarginLayoutParams ?: return
    params.topMargin = top
    params.leftMargin = left
    params.rightMargin = right
    params.bottomMargin = bottom
    layoutParams = params
}

fun ViewGroup.findChild(filter: (View) -> Boolean): View? {
    var child: View? = null

    forEachRecursively {
        if (filter(it)) {
            child = it
            return@forEachRecursively
        }
    }

    return child
}

@Suppress("UNCHECKED_CAST")
fun <T : View> View.findViewByIdNotRecursive(id: Int): T? {
    if (this is ViewGroup) {
        forEach { child ->
            if (child.id == id) {
                return child as T
            }
        }
    }
    return null
}

inline fun <reified T : View> View.findParentByType(): T? {
    var currentParent: ViewParent? = parent
    while (currentParent != null) {
        if (currentParent is T) {
            return currentParent
        }
        currentParent = currentParent.parent
    }
    return null
}

fun <T> ViewGroup.map(action: (View) -> T): List<T> {
    val result = mutableListOf<T>()
    forEach {
        result.add(action(it))
    }
    return result
}

// TODO please remove
val View.lifecycleScope: LifecycleCoroutineScope
    get() = findFragment<Fragment>().viewLifecycleOwner.lifecycleScope

fun View.launchWhenResumed(block: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launchWhenResumed(block)
}

// TODO check
fun View.onClick(block: suspend (View) -> Unit) {
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var job by autoDisposeJob()
    setOnClickListener {
        job = launchWhenResumed {
            block(it)
        }
    }
}