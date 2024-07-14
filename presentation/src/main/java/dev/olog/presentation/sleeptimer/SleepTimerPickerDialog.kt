package dev.olog.presentation.sleeptimer

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.interactor.SleepTimerUseCase
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.utils.TimeUtils
import dev.olog.shared.flowInterval
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SleepTimerPickerDialog : ScrollHmsPickerDialog(),
    ScrollHmsPickerDialog.HmsPickHandler {

    private var countDownDisposable: Job? = null

    private lateinit var fakeView: View
    private lateinit var okButton: Button

    @Inject
    lateinit var sleepTimerUseCase: SleepTimerUseCase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!

        okButton = view.findViewById(R.id.button_ok)

        val sleepModel = sleepTimerUseCase.getLast()
        val sleepTime = sleepModel.sleepTime
        val sleepFrom = sleepModel.fromWhen

        setTimeInMilliseconds(sleepTime - (System.currentTimeMillis() - sleepFrom), false)

        fakeView = view.findViewById(R.id.fakeView)
        toggleVisibility(fakeView, sleepTime > 0)

        toggleButtons(sleepTime > 0)

        if (sleepTime > 0) {

            countDownDisposable = lifecycleScope.launch {
                try {
                    flowInterval(1, TimeUnit.SECONDS)
                        .map { sleepTime - (System.currentTimeMillis() - sleepFrom) }
                        .takeWhile { it >= 0L }
                        .collect {
                            setTimeInMilliseconds(it, true)
                        }
                } catch (ex: Throwable) {
                    resetAlarmManager()
                    toggleButtons(false)
                }
            }
        }

        pickListener = this

        return view
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            if (it.isSelected) {
                // as reset button
                setTimeInMilliseconds(0, true)
                countDownDisposable?.cancel()
                toggleButtons(false)
                resetAlarmManager()
                resetAlarmManager()
            } else {
                // as ok button
                if (TimeUtils.timeAsMillis(
                        hmsPicker.hours,
                        hmsPicker.minutes,
                        hmsPicker.seconds
                    ) > 0
                ) {
                    pickListener?.onHmsPick(
                        reference,
                        hmsPicker.hours,
                        hmsPicker.minutes,
                        hmsPicker.seconds
                    )
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
        countDownDisposable?.cancel()
    }

    private fun toggleButtons(isCountDown: Boolean) {
        val okText = if (isCountDown) {
            R.string.scroll_hms_picker_stop
        } else android.R.string.ok

        okButton.setText(okText)
        okButton.isSelected = isCountDown
        toggleVisibility(fakeView, isCountDown)
    }

    private fun toggleVisibility(view: View, showCondition: Boolean) {
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
        val sleepTime = TimeUtils.timeAsMillis(hours, minutes, seconds)
        val sleepFrom = System.currentTimeMillis()

        val nextSleep = SystemClock.elapsedRealtime() +
                TimeUnit.HOURS.toMillis(hours.toLong()) +
                TimeUnit.MINUTES.toMillis(minutes.toLong()) +
                TimeUnit.SECONDS.toMillis(seconds.toLong())

        sleepTimerUseCase.set(sleepFrom, sleepTime, nextSleep)
    }

    private fun resetAlarmManager() {
        sleepTimerUseCase.reset()
    }

}