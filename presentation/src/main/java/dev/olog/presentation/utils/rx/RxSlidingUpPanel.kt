package dev.olog.presentation.utils.rx

import com.sothree.slidinguppanel.SlidingUpPanelLayout

import io.reactivex.Observable

object RxSlidingUpPanel {

    fun panelStateEvents(slidingUpPanelLayout: SlidingUpPanelLayout): Observable<SlidingPanelState> {
        return SlidingUpPanelEventsObservable(slidingUpPanelLayout)
    }

}
