package dev.olog.core.gateway

interface AlarmService {

    fun set(sleepUntil: Long)

    fun resetTimer()

}