package dev.olog.feature.media.api.interactor

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.feature.media.api.FeatureMediaNavigator
import javax.inject.Inject

class SleepTimerUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gateway: AppPreferencesGateway,
    private val featureMediaNavigator: FeatureMediaNavigator,
) {

    companion object {
        const val ACTION_STOP_SLEEP_END = "action.stop_sleep_timer"
    }

    fun getLast(): SleepData = SleepData(
        gateway.getSleepFrom(),
        gateway.getSleepTime()
    )

    fun set(sleepFrom: Long, sleepTime: Long, nextSleep: Long) {
        gateway.setSleepTimer(sleepFrom, sleepTime)

        val intent = stopMusicServiceIntent()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextSleep, intent)
    }

    fun reset() {
        gateway.resetSleepTimer()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(stopMusicServiceIntent())
    }

    private fun stopMusicServiceIntent(): PendingIntent {
        return featureMediaNavigator.pendingIntent(ACTION_STOP_SLEEP_END)
    }

}

data class SleepData(
    val fromWhen: Long,
    val sleepTime: Long
)