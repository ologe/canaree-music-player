package dev.olog.core.interactor

import android.app.AlarmManager
import android.content.Context
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.shared.PendingIntents
import javax.inject.Inject

class SleepTimerUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gateway: AppPreferencesGateway
) {

    fun getLast(): SleepData = SleepData(
        gateway.getSleepFrom(),
        gateway.getSleepTime()
    )

    fun set(sleepFrom: Long, sleepTime: Long, nextSleep: Long) {
        gateway.setSleepTimer(sleepFrom, sleepTime)

        val intent = PendingIntents.stopMusicServiceIntent(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextSleep, intent)
    }

    fun reset() {
        gateway.resetSleepTimer()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(PendingIntents.stopMusicServiceIntent(context))
    }

}

data class SleepData(
    val fromWhen: Long,
    val sleepTime: Long
)