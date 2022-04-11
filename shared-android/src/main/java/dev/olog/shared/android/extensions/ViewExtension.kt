@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.annotation.Px
import androidx.core.view.*

inline fun View.toggleSelected() {
    this.isSelected = !this.isSelected
}

// TODO is not recursive
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
    val params = this.layoutParams as? ViewGroup.MarginLayoutParams ?: return
    params.height = heightPx
    layoutParams = params
}

fun View.setWidth(@Px widthPx: Int) {
    val params = this.layoutParams as? ViewGroup.MarginLayoutParams ?: return
    params.width = widthPx
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

fun<T> ViewGroup.map(action: (View) -> T): List<T> {
    val result = mutableListOf<T>()
    forEach {
        result.add(action(it))
    }
    return result
}