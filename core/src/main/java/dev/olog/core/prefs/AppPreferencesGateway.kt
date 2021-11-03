package dev.olog.core.prefs

import dev.olog.core.Prefs
import dev.olog.core.entity.UserCredentials
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AppPreferencesGateway : Prefs {

    fun resetSleepTimer()
    fun setSleepTimer(sleepFrom: Long, sleepTime: Long)
    fun getSleepTime() : Long
    fun getSleepFrom() : Long

    fun canAutoCreateImages(): Boolean

    fun getLastFmCredentials(): UserCredentials
    fun observeLastFmCredentials(): Flow<UserCredentials>
    fun setLastFmCredentials(user: UserCredentials)

    fun observeDefaultMusicFolder(): Flow<File>
    fun getDefaultMusicFolder(): File
    fun setDefaultMusicFolder(file: File)

    fun setDefault()
}

