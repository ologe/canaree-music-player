package dev.olog.core.prefs

import dev.olog.core.preference.Preference
import dev.olog.core.preference.Prefs
import java.io.File

interface AppPreferencesGateway : Prefs {

    fun resetSleepTimer()
    fun setSleepTimer(sleepFrom: Long, sleepTime: Long)
    fun getSleepTime() : Long
    fun getSleepFrom() : Long

    fun canAutoCreateImages(): Boolean

    val defaultMusicFolder: Preference<File>

    fun setDefault()
}

