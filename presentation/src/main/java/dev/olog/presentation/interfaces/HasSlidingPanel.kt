package dev.olog.presentation.interfaces

import android.content.Context
import androidx.fragment.app.Fragment
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import dev.olog.platform.extension.findInContext

interface HasSlidingPanel {

    fun getSlidingPanel(): MultiListenerBottomSheetBehavior<*>

}

val Context.slidingPanel: MultiListenerBottomSheetBehavior<*>
    get() = this.findInContext<HasSlidingPanel>().getSlidingPanel()

inline val Fragment.slidingPanel: MultiListenerBottomSheetBehavior<*>
    get() = requireContext().slidingPanel