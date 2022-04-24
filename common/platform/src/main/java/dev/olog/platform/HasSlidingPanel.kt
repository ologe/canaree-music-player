package dev.olog.platform

import com.google.android.material.bottomsheet.BottomSheetBehavior

interface HasSlidingPanel {

    fun getSlidingPanel(): BottomSheetBehavior<*>

}
