package dev.olog.core.gateway

interface AlarmService {

    fun set(nextSleep: Long)

    fun resetTimer()

}