package dev.olog.presentation.interfaces

import android.content.Context
import androidx.fragment.app.Fragment
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import dev.olog.shared.android.extensions.findInContext

interface HasSlidingPanel {

    fun getSlidingPanel(): MultiListenerBottomSheetBehavior<*>

}

val Context.slidingPanel: MultiListenerBottomSheetBehavior<*>
    get() = this.findInContext<HasSlidingPanel>().getSlidingPanel()

inline val Fragment.slidingPanel: MultiListenerBottomSheetBehavior<*>
    get() = requireContext().slidingPanel