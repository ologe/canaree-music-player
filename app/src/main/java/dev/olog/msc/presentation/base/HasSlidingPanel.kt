package dev.olog.msc.presentation.base

import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior

interface HasSlidingPanel {

    fun getSlidingPanel(): MultiListenerBottomSheetBehavior<*>

}
