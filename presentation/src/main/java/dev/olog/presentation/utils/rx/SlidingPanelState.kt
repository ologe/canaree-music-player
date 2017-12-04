package dev.olog.presentation.utils.rx

import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState

data class SlidingPanelState(
        val previousState: PanelState,
        val newState: PanelState
)
