package dev.olog.presentation.utils.rx

import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED
import dev.olog.shared_android.assertMainThread
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

internal class SlidingUpPanelEventsObservable(
        private val view: SlidingUpPanelLayout

) : Observable<SlidingPanelState>() {

    override fun subscribeActual(observer: Observer<in SlidingPanelState>) {
        assertMainThread()
        val listener = Listener(view, observer)
        observer.onSubscribe(listener)
        view.addPanelSlideListener(listener.panelSlideListener)
    }

    internal inner class Listener(
            private val slidingUpPanelLayout: SlidingUpPanelLayout,
            observer: Observer<in SlidingPanelState>

    ) : MainThreadDisposable() {

        val panelSlideListener = object : SlidingUpPanelLayout.SimplePanelSlideListener() {
            override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
                if (!isDisposed) {
                    observer.onNext(SlidingPanelState(previousState!!, newState!!))
                }
            }
        }

        init {
            observer.onNext(SlidingPanelState(COLLAPSED, COLLAPSED))
        }

        override fun onDispose() {
            slidingUpPanelLayout.removePanelSlideListener(panelSlideListener)
        }
    }
}
