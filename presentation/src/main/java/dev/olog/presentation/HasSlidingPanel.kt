package dev.olog.presentation

import com.sothree.slidinguppanel.SlidingUpPanelLayout

interface HasSlidingPanel {

    fun getSlidingPanel(): SlidingUpPanelLayout?

}

fun SlidingUpPanelLayout?.isCollapsed() = this != null && panelState == SlidingUpPanelLayout.PanelState.COLLAPSED
fun SlidingUpPanelLayout?.isExpanded() = this != null && panelState != SlidingUpPanelLayout.PanelState.COLLAPSED

fun SlidingUpPanelLayout?.collapse() {
    if (this != null){
        panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }

}
fun SlidingUpPanelLayout?.expand() {
    if (this != null){
        panelState = SlidingUpPanelLayout.PanelState.EXPANDED
    }
}