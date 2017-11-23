package dev.olog.presentation.utils.rx;

import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

public class SlidingPanelState {

    private final PanelState previousState;
    private final PanelState newState;

    public SlidingPanelState(PanelState previousState, PanelState newState) {
        this.previousState = previousState;
        this.newState = newState;
    }

    public PanelState previousState() {
        return previousState;
    }

    public PanelState newState() {
        return newState;
    }

}
