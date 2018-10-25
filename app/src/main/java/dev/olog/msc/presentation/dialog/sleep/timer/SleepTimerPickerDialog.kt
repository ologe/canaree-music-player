package dev.olog.msc.presentation.dialog.sleep.timer

import android.app.AlarmManager
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import dev.olog.msc.R
import dev.olog.msc.domain.interactor.prefs.SleepTimerUseCase
import dev.olog.msc.utils.PendingIntents
import dev.olog.msc.utils.TimeUtils
import dev.olog.msc.utils.k.extension.act
import dev.olog.msc.utils.k.extension.logStackStace
import dev.olog.msc.utils.k.extension.toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SleepTimerPickerDialog : ScrollHmsPickerDialog(), ScrollHmsPickerDialog.HmsPickHandler {

    private var countDownDisposable: Disposable? = null

    private lateinit var fakeView: View
    private lateinit var okButton: Button

    @Inject lateinit var alarmManager: AlarmManager
    @Inject lateinit var sleepTimerUseCase: SleepTimerUseCase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!

        okButton = view.findViewById(R.id.button_ok)

        val (sleepFrom, sleepTime) = sleepTimerUseCase.getLast()

        setTimeInMilliseconds(sleepTime - (System.currentTimeMillis() - sleepFrom), false)

        fakeView = view.findViewById(R.id.fakeView)
        toggleVisibility(fakeView, sleepTime > 0)

        toggleButtons(sleepTime > 0)

        if (sleepTime > 0){
            countDownDisposable = Observable.interval(1, TimeUnit.SECONDS, Schedulers.computation())
                    .map { sleepTime - (System.currentTimeMillis() - sleepFrom) }
                    .takeWhile { it >= 0L }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        setTimeInMilliseconds(it, true)
                    }, Throwable::logStackStace, {
                        resetPersistedValues()
                        toggleButtons(false)
                    })
        }

        pickListener = this

        return view
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            if (it.isSelected){
                // as reset button
                setTimeInMilliseconds(0, true)
                countDownDisposable?.dispose()
                toggleButtons(false)
                resetPersistedValues()
                resetAlarmManager()
            } else {
                // as ok button
                if(TimeUtils.timeAsMillis(hmsPicker.hours, hmsPicker.minutes, hmsPicker.seconds) > 0){
                    pickListener?.onHmsPick(reference, hmsPicker.hours, hmsPicker.minutes, hmsPicker.seconds)
                    act.toast(R.string.sleep_timer_set)
                    dismiss()
                } else {
                    act.toast(R.string.sleep_timer_can_not_be_set)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownDisposable?.dispose()
    }

    private fun toggleButtons(isCountDown: Boolean){
        val okText = if (isCountDown){
            R.string.scroll_hms_picker_stop
        } else android.R.string.ok

        okButton.setText(okText)
        okButton.isSelected = isCountDown
        toggleVisibility(fakeView, isCountDown)
    }

    private fun toggleVisibility(view: View, showCondition: Boolean){
        val visibility = if (showCondition) View.VISIBLE else View.GONE
        view.visibility = visibility
    }

    private fun setTimeInMilliseconds(millis: Long, smooth: Boolean) {
        val totalSeconds = (millis / 1000).toInt()

        val hours = totalSeconds / 3600
        val remaining = totalSeconds % 3600
        val minutes = remaining / 60
        val seconds = remaining % 60
        hmsPicker.setTime(hours, minutes, seconds, smooth)
    }

    override fun onHmsPick(reference: Int, hours: Int, minutes: Int, seconds: Int) {
        val sleep = TimeUtils.timeAsMillis(hours, minutes, seconds)
        val currentTime = System.currentTimeMillis()

        persistValues(currentTime, sleep)
        setAlarmManager(hours, minutes, seconds)
    }

    private fun resetPersistedValues(){
        persistValues(-1, -1)
    }

    private fun persistValues(sleepFrom: Long, sleepTime: Long){
        sleepTimerUseCase.set(sleepFrom, sleepTime)
    }

    private fun resetAlarmManager(){
        val intent = PendingIntents.stopMusicServiceIntent(context!!)
        alarmManager.cancel(intent)
    }

    private fun setAlarmManager(hours: Int, minutes: Int, seconds: Int){
        val nextSleep = SystemClock.elapsedRealtime() +
                TimeUnit.HOURS.toMillis(hours.toLong()) +
                TimeUnit.MINUTES.toMillis(minutes.toLong()) +
                TimeUnit.SECONDS.toMillis(seconds.toLong())

        val intent = PendingIntents.stopMusicServiceIntent(context!!)
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextSleep, intent)
    }

}