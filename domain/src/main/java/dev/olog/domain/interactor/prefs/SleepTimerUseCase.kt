package dev.olog.domain.interactor.prefs

import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import javax.inject.Inject

class SleepTimerUseCase @Inject constructor(
        private val gateway: AppPreferencesGateway
){

    fun getLast() = gateway.getSleepTimer()

    fun set(millis: Long){
        gateway.setSleepTimer(millis)
    }

    fun reset(){
        gateway.resetSleepTimer()
    }

}