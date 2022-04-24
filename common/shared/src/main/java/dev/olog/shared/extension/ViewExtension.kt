package dev.olog.shared.extension

import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.annotation.Px
import androidx.core.view.doOnDetach
import androidx.core.view.forEach
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import dev.olog.shared.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

@Suppress("NOTHING_TO_INLINE")
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
    val params = this.layoutParams ?: return
    params.height = heightPx
    layoutParams = params
}

fun View.setWidth(@Px widthPx: Int) {
    val params = this.layoutParams ?: return
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
// TODO deprecate
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

val View.coroutineScope: CoroutineScope
    get() {
        require(isAttachedToWindow)
        var scope = getTag(R.id.view_scope) as CoroutineScope?
        if (scope == null) {
            scope = ViewScope(this).also {
                setTag(R.id.view_scope, it)
            }
        }
        return scope
    }

class ViewScope(private val view: View) : CoroutineScope {

    init {
        view.doOnDetach {
            delegate.cancel()
            view.setTag(R.id.view_scope, null)
        }
    }

    private val delegate = MainScope()

    override val coroutineContext: CoroutineContext
        get() = delegate.coroutineContext
}