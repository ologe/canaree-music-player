@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.DimenRes
import androidx.annotation.Px
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.*
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


fun View.toggleVisibility(visible: Boolean, gone: Boolean) {
    if (visible) {
        this.visibility = View.VISIBLE
    } else {
        if (gone) {
            this.visibility = View.GONE
        } else {
            this.visibility = View.INVISIBLE
        }
    }
}

inline fun View.setGone() {
    this.visibility = View.GONE
}

inline fun View.setVisible() {
    this.visibility = View.VISIBLE
}

inline fun View.setInvisible() {
    this.visibility = View.INVISIBLE
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
    when (params) {
        is FrameLayout.LayoutParams -> params.height = heightPx
        is LinearLayout.LayoutParams -> params.height = heightPx
        is RelativeLayout.LayoutParams -> params.height = heightPx
        is CoordinatorLayout.LayoutParams -> params.height = heightPx
        is ConstraintLayout.LayoutParams -> params.height = heightPx
    }
    layoutParams = params
}

fun View.setWidth(@Px heightPx: Int) {
    val params = this.layoutParams
    when (params) {
        is FrameLayout.LayoutParams -> params.width = heightPx
        is LinearLayout.LayoutParams -> params.width = heightPx
        is RelativeLayout.LayoutParams -> params.width = heightPx
        is CoordinatorLayout.LayoutParams -> params.width = heightPx
        is ConstraintLayout.LayoutParams -> params.width = heightPx
    }
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

fun View.findActivity(): FragmentActivity {
    var context: Context = context
    while (context is ContextWrapper) {
        if (context is FragmentActivity) {
            return context
        }
        context = context.baseContext
    }
    error("View $this does not have a FragmentActivity set")
}

suspend fun View.awaitOnAttach() = suspendCancellableCoroutine<Unit> { continuation ->
    if (isAttachedToWindow) {
        continuation.resume(Unit)
    } else {
        val listener = object : View.OnAttachStateChangeListener {

            override fun onViewAttachedToWindow(v: View) {
                removeOnAttachStateChangeListener(this)
                continuation.resume(Unit)
            }

            override fun onViewDetachedFromWindow(v: View) {}
        }
        addOnAttachStateChangeListener(listener)
        continuation.invokeOnCancellation { removeOnAttachStateChangeListener(listener) }
    }
}

inline fun View.dip(value: Int): Int = context.dip(value)
inline fun View.dipf(value: Int): Float = context.dipf(value)
inline fun View.dimen(@DimenRes resource: Int): Int = context.dimen(resource)