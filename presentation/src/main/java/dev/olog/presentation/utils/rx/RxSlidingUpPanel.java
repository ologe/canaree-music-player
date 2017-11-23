package dev.olog.presentation.utils.rx;

import android.support.annotation.NonNull;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import io.reactivex.Observable;

public class RxSlidingUpPanel {

    public static Observable<SlidingPanelState> panelStateEvents(@NonNull SlidingUpPanelLayout slidingUpPanelLayout){
        return new SlidingUpPanelEventsObservable(slidingUpPanelLayout);
    }

}
