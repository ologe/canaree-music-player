package dev.olog.msc.presentation.base

import android.util.Log
import com.sothree.slidinguppanel.SlidingUpPanelLayout

interface HasSlidingPanel {

    fun getSlidingPanel(): SlidingUpPanelLayout?

    fun addSlidingPanel(listener: SlidingUpPanelLayout.PanelSlideListener){
        getSlidingPanel()?.addPanelSlideListener(listener)
                ?: Log.w("Sliding Panel", "sliding panel not found")
    }

    fun removeSlidingPanel(listener: SlidingUpPanelLayout.PanelSlideListener){
        getSlidingPanel()?.removePanelSlideListener(listener)
                ?: Log.w("Sliding Panel", "sliding panel not found")
    }

}

fun SlidingUpPanelLayout?.isCollapsed() = this != null &&
        panelState == SlidingUpPanelLayout.PanelState.COLLAPSED
fun SlidingUpPanelLayout?.isExpanded() = this != null &&
        panelState != SlidingUpPanelLayout.PanelState.COLLAPSED &&
        panelState != SlidingUpPanelLayout.PanelState.HIDDEN

fun SlidingUpPanelLayout?.collapse() {
    if (this != null && panelState != SlidingUpPanelLayout.PanelState.HIDDEN){
        panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }
}

fun SlidingUpPanelLayout?.expand() {
    if (this != null && panelState != SlidingUpPanelLayout.PanelState.HIDDEN){
        panelState = SlidingUpPanelLayout.PanelState.EXPANDED
    }
}