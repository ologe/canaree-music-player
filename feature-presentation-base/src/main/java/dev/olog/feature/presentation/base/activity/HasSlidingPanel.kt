package dev.olog.feature.presentation.base.activity

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

interface HasSlidingPanel {

    fun getSlidingPanel(): BottomSheetBehavior<*>
    fun getSlidingPanelView(): View

}
