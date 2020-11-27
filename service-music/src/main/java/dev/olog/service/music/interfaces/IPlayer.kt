package dev.olog.service.music.interfaces

import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.model.SkipType

internal interface IPlayer : IPlayerLifecycle {

    suspend fun prepare(playerModel: PlayerMediaEntity)
    suspend fun playNext(playerModel: PlayerMediaEntity, skipType: SkipType)
    suspend fun play(playerModel: PlayerMediaEntity)

    suspend fun seekTo(millis: Long)

    suspend fun resume()
    suspend fun pause(stopService: Boolean, releaseFocus: Boolean = true)

    fun isPlaying(): Boolean
    fun getBookmark(): Long

    suspend fun forwardTenSeconds()
    suspend fun replayTenSeconds()

    suspend fun forwardThirtySeconds()
    suspend fun replayThirtySeconds()

    suspend fun stopService()

    fun setVolume(volume: Float)
}

