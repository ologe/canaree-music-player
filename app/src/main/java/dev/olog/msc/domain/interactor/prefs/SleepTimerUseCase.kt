package dev.olog.msc.domain.interactor.prefs

import dev.olog.core.prefs.AppPreferencesGateway
import javax.inject.Inject

class SleepTimerUseCase @Inject constructor(private val gateway: AppPreferencesGateway){

    fun getLast(): SleepData = SleepData(
            gateway.getSleepFrom(),
            gateway.getSleepTime()
    )

    fun set(sleepFrom: Long, sleepTime: Long){
        gateway.setSleepTimer(sleepFrom, sleepTime)
    }

    fun reset(){
        gateway.resetSleepTimer()
    }

}

data class SleepData(
        val fromWhen: Long,
        val sleepTime: Long
)