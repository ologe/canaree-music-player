package dev.olog.presentation.widgets;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.widget.SeekBar;

import java.util.concurrent.TimeUnit;

import dev.olog.presentation.utils.TextUtils;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

import static dev.olog.shared.RxUtilsKt.unsubscribe;

public class RxSeekBar extends AppCompatSeekBar {

    private static final int INTERVAL = 250;

    enum Notification {
        START, STOP
    }

    private PublishSubject<Integer> onProgressChanged = PublishSubject.create();
    private PublishSubject<Notification> onStateChanged = PublishSubject.create();

    private Disposable updateDisposable;

    public RxSeekBar(Context context) {
        super(context);
    }

    public RxSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RxSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setOnSeekBarChangeListener(listener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setOnSeekBarChangeListener(null);
        unsubscribe(updateDisposable);
    }

    public void handleState(boolean isPlaying){
        unsubscribe(updateDisposable);
        if (isPlaying) {
            resume();
        }
    }

    private void resume(){
        updateDisposable = Observable.interval(INTERVAL, TimeUnit.MILLISECONDS)
                .map(aLong -> INTERVAL)
                .subscribe(this::incrementProgressBy);
    }

    private OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            onProgressChanged.onNext(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            onStateChanged.onNext(Notification.START);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            onStateChanged.onNext(Notification.STOP);
        }
    };

    public Observable<String> observeChanges(){
        return Observable.defer(() -> onProgressChanged.map(TextUtils::getReadableSongLength));
    }

    public Observable<SeekBar> observeStopTrackingTouch(){
        return Observable.defer(() -> onStateChanged
                .filter(notification -> notification == Notification.STOP)
                .map(notification -> this));
    }

}
