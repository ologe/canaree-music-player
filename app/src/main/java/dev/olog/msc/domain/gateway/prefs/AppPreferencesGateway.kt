package dev.olog.msc.domain.gateway.prefs

import dev.olog.msc.domain.entity.*
import io.reactivex.Completable
import io.reactivex.Observable
import java.io.File

interface AppPreferencesGateway {

    fun resetSleepTimer()
    fun setSleepTimer(sleepFrom: Long, sleepTime: Long)
    fun getSleepTime() : Long
    fun getSleepFrom() : Long

    fun observePlayerControlsVisibility(): Observable<Boolean>
    fun observeAutoCreateImages(): Observable<Boolean>

    fun getLastFmCredentials(): UserCredentials
    fun observeLastFmCredentials(): Observable<UserCredentials>
    fun setLastFmCredentials(user: UserCredentials)

    fun getSyncAdjustment(): Long
    fun setSyncAdjustment(value: Long)

    fun observeDefaultMusicFolder(): Observable<File>
    fun getDefaultMusicFolder(): File
    fun setDefaultMusicFolder(file: File)

    fun isAdaptiveColorEnabled(): Boolean

    fun setDefault(): Completable
}

