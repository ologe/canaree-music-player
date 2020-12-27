package dev.olog.shared.android

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import dev.olog.shared.android.extensions.findActivity

val FragmentActivity.slidingPanel: BottomSheetBehavior<*>
    get() = (this as SlidingPanelAmbient).getSlidingPanel()

val Fragment.slidingPanel: BottomSheetBehavior<*>
    get() = requireActivity().slidingPanel

val View.slidingPanel: BottomSheetBehavior<*>
    get() = findActivity().slidingPanel

interface SlidingPanelAmbient {

    fun getSlidingPanel(): BottomSheetBehavior<*>

}

@Suppress("NOTHING_TO_INLINE")
inline fun BottomSheetBehavior<*>?.isCollapsed() = this?.state == STATE_COLLAPSED || this?.state == STATE_HIDDEN

@Suppress("NOTHING_TO_INLINE")
inline fun BottomSheetBehavior<*>?.isExpanded() = this?.state == STATE_EXPANDED

@Suppress("NOTHING_TO_INLINE")
inline fun BottomSheetBehavior<*>?.collapse() {
    this?.state = STATE_COLLAPSED
}

@Suppress("NOTHING_TO_INLINE")
inline fun BottomSheetBehavior<*>?.expand() {
    this?.state = STATE_EXPANDED
}