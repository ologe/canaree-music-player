@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.widgets.extension

import com.google.android.material.bottomsheet.BottomSheetBehavior

inline fun BottomSheetBehavior<*>?.isCollapsed() = this != null &&
        (state == BottomSheetBehavior.STATE_COLLAPSED || state == BottomSheetBehavior.STATE_HIDDEN)

inline fun BottomSheetBehavior<*>?.isExpanded() = this != null && state == BottomSheetBehavior.STATE_EXPANDED

inline fun BottomSheetBehavior<*>?.collapse() {
    if (this != null){
        state = BottomSheetBehavior.STATE_COLLAPSED
    }
}

inline fun BottomSheetBehavior<*>?.expand() {
    if (this != null){
        state = BottomSheetBehavior.STATE_EXPANDED
    }
}