package dev.olog.shared_android.rx

import android.widget.SeekBar
import com.jakewharton.rxbinding2.InitialValueObservable
import dev.olog.shared_android.assertMainThread
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

class SeekBarObservable(
        private val view: SeekBar

) : InitialValueObservable<Any>() {

    enum class Notification {
        START, STOP
    }

    override fun subscribeListener(observer: Observer<in Any>) {
        assertMainThread()
        val listener = Listener(view, observer)
        view.setOnSeekBarChangeListener(listener)
        observer.onSubscribe(listener)
    }

    override fun getInitialValue(): Any = view.progress

    class Listener(
            private val view: SeekBar,
            private val observer: Observer<in Any>

    ) : MainThreadDisposable(), SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (!isDisposed){
                observer.onNext(progress)
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            if (!isDisposed){
                observer.onNext(Pair(Notification.START, seekBar.progress))
            }
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            if (!isDisposed){
                observer.onNext(Pair(Notification.STOP, seekBar.progress))
            }
        }

        override fun onDispose() {
            view.setOnSeekBarChangeListener(null)
        }

    }

}