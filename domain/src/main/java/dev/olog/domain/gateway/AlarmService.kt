package dev.olog.domain.gateway

interface AlarmService {

    fun set(sleepUntil: Long)

    fun resetTimer()

}