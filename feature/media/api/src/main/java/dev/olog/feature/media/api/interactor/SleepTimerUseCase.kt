package dev.olog.feature.media.api.interactor

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.PendingIntentFactory
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.feature.media.api.FeatureMediaNavigator
import dev.olog.feature.media.api.MusicConstants
import javax.inject.Inject

class SleepTimerUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gateway: AppPreferencesGateway,
    private val pendingIntentFactory: PendingIntentFactory,
    private val featureMediaNavigator: FeatureMediaNavigator,
) {

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
        val intent = featureMediaNavigator.createIntent(MusicConstants.ACTION_STOP_SLEEP_END)
        return pendingIntentFactory.createForService(intent)
    }

}

data class SleepData(
    val fromWhen: Long,
    val sleepTime: Long
)