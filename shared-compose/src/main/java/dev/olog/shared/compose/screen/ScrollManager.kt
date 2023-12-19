package dev.olog.shared.compose.screen

import android.app.Activity
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.compose.R

@Composable
internal fun rememberScrollManager(): ScrollManager {
    if (LocalInspectionMode.current) {
        val view = LocalView.current
        return remember(view) { view.scrollManager }
    }
    val context = LocalContext.current
    return remember(context) {
        context.findInContext<Activity>().scrollManager
    }
}

internal fun Modifier.dispatchListScroll() = composed {
    val scrollManager = rememberScrollManager()

    Modifier.nestedScroll(object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            scrollManager.dispatchListScroll(available.y)
            return super.onPreScroll(available, source)
        }
    })
}

val Fragment.scrollManager: ScrollManager
    get() = requireActivity().scrollManager

val Activity.scrollManager: ScrollManager
    get() = window.decorView.scrollManager

private val View.scrollManager: ScrollManager
    get() {
        val scrollManager = getTag(R.id.scroll_manager) as ScrollManager?
        if (scrollManager != null) {
            return scrollManager
        }
        return ScrollManager().also { setTag(R.id.scroll_manager, it) }
    }

class ScrollManager {

    private val listeners = mutableSetOf<(Float) -> Unit>()

    fun dispatchListScroll(dy: Float) {
        for (listener in listeners) {
            listener(dy)
        }
    }

    fun registerSlidingPanelCallback(
        behavior: BottomSheetBehavior<*>,
        callback: (slideOffset: Float) -> Unit,
    ) {
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                callback(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {}
        }
        behavior.addBottomSheetCallback(bottomSheetCallback)
    }

    fun addScrollListener(callback: (Float) -> Unit) {
        listeners += callback
    }

    fun removeScrollListener(callback: (Float) -> Unit) {
        listeners -= callback
    }

}