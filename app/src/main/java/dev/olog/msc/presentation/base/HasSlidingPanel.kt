package dev.olog.msc.presentation.base

import android.util.Log
import com.sothree.slidinguppanel.SlidingUpPanelLayout

interface HasSlidingPanel {

    fun getSlidingPanel(): SlidingUpPanelLayout?

    fun addPanelSlideListener(listener: SlidingUpPanelLayout.PanelSlideListener){
        getSlidingPanel()?.addPanelSlideListener(listener)
                ?: Log.w("Sliding Panel", "sliding panel not found")
    }

    fun removePanelSlideListener(listener: SlidingUpPanelLayout.PanelSlideListener){
        getSlidingPanel()?.removePanelSlideListener(listener)
                ?: Log.w("Sliding Panel", "sliding panel not found")
    }

}
