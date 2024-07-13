package dev.olog.core.interactor

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.intents.Classes
import dev.olog.shared.android.PendingIntentFactory
import javax.inject.Inject

class SleepTimerUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gateway: AppPreferencesGateway
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

        val intent = stopMusicServiceIntent(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextSleep, intent)
    }

    fun reset() {
        gateway.resetSleepTimer()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(stopMusicServiceIntent(context))
    }

    private fun stopMusicServiceIntent(context: Context): PendingIntent {
        val intent = Intent(context, Class.forName(Classes.SERVICE_MUSIC))
        intent.action = ACTION_STOP_SLEEP_END
        return PendingIntentFactory.ofForegroundService(
            context = context,
            intent = intent
        )
    }

}

data class SleepData(
    val fromWhen: Long,
    val sleepTime: Long
)