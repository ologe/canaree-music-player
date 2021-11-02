package dev.olog.feature.base

import com.google.android.material.bottomsheet.BottomSheetBehavior

interface HasSlidingPanel {

    fun getSlidingPanel(): BottomSheetBehavior<*>

}
