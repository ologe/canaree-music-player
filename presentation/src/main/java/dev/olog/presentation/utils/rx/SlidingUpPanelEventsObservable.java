package dev.olog.presentation.utils.rx;

import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED;
import static dev.olog.presentation.utils.ThreadUtilsKt.assertMainThread;

class SlidingUpPanelEventsObservable extends Observable<SlidingPanelState> {

    private final SlidingUpPanelLayout view;

    SlidingUpPanelEventsObservable(SlidingUpPanelLayout view) {
        this.view = view;
    }

    @Override
    protected void subscribeActual(Observer observer) {
        assertMainThread();
        Listener listener = new Listener(view, observer);
        observer.onSubscribe(listener);
        view.addPanelSlideListener(listener.panelSlideListener);
    }

    final class Listener extends MainThreadDisposable {

        private final SlidingUpPanelLayout slidingUpPanelLayout;
        private final SlidingUpPanelLayout.PanelSlideListener panelSlideListener;

        Listener(SlidingUpPanelLayout slidingUpPanelLayout, Observer<SlidingPanelState> observer) {

            observer.onNext(new SlidingPanelState(COLLAPSED, COLLAPSED));

            this.slidingUpPanelLayout = slidingUpPanelLayout;
            this.panelSlideListener = new SlidingUpPanelLayout.SimplePanelSlideListener(){
                @Override
                public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                    if (!isDisposed()){
                        observer.onNext(new SlidingPanelState(previousState, newState));
                    }
                }
            };
        }

        @Override
        protected void onDispose() {
            slidingUpPanelLayout.removePanelSlideListener(panelSlideListener);
        }
    }
}
