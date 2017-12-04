package dev.olog.presentation.widgets

import android.content.Context
import android.support.v7.widget.AppCompatSeekBar
import android.util.AttributeSet
import android.widget.SeekBar
import dev.olog.presentation.utils.TextUtils
import dev.olog.shared.unsubscribe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class RxSeekBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0

) : AppCompatSeekBar(context, attrs, defStyleAttr) {

    companion object {
        private const val INTERVAL = 250L
    }

    enum class Notification {
        START, STOP
    }

    private val onProgressChanged = PublishSubject.create<Int>()
    private val onStateChanged = PublishSubject.create<Notification>()

    private var updateDisposable: Disposable? = null

    private val listener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            onProgressChanged.onNext(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            onStateChanged.onNext(Notification.START)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            onStateChanged.onNext(Notification.STOP)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setOnSeekBarChangeListener(listener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnSeekBarChangeListener(null)
        updateDisposable.unsubscribe()
    }

    fun handleState(isPlaying: Boolean) {
        updateDisposable.unsubscribe()
        if (isPlaying) {
            resume()
        }
    }

    private fun resume() {
        updateDisposable = Observable.interval(INTERVAL, TimeUnit.MILLISECONDS)
                .subscribe({ this.incrementProgressBy(INTERVAL.toInt()) }, Throwable::printStackTrace)
    }

    fun observeChanges(): Observable<String> {
        return Observable.defer {
            onProgressChanged
                    .map { it.toLong() }
                    .map { TextUtils.getReadableSongLength(it) }
        }
    }

    fun observeStopTrackingTouch(): Observable<SeekBar> {
        return Observable.defer {
            onStateChanged.filter { notification -> notification == Notification.STOP }
                    .map { this }
        }
    }

}
