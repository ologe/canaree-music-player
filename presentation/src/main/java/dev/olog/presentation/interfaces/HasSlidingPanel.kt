package dev.olog.presentation.interfaces

import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior

interface HasSlidingPanel {

    fun getSlidingPanel(): MultiListenerBottomSheetBehavior<*>

}
