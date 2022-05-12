package dev.olog.ui.extension

import com.google.android.material.bottomsheet.BottomSheetBehavior

@Suppress("NOTHING_TO_INLINE")
inline fun BottomSheetBehavior<*>?.isCollapsed() = this != null &&
        (state == BottomSheetBehavior.STATE_COLLAPSED || state == BottomSheetBehavior.STATE_HIDDEN)

@Suppress("NOTHING_TO_INLINE")
inline fun BottomSheetBehavior<*>?.isExpanded() = this != null && state == BottomSheetBehavior.STATE_EXPANDED

@Suppress("NOTHING_TO_INLINE")
inline fun BottomSheetBehavior<*>?.collapse() {
    if (this != null){
        state = BottomSheetBehavior.STATE_COLLAPSED
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun BottomSheetBehavior<*>?.expand() {
    if (this != null){
        state = BottomSheetBehavior.STATE_EXPANDED
    }
}