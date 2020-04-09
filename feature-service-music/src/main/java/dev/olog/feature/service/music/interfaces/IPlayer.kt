package dev.olog.feature.service.music.interfaces

import dev.olog.feature.service.music.model.PlayerMediaEntity
import dev.olog.feature.service.music.model.SkipType

internal interface IPlayer : IPlayerLifecycle {

    fun prepare(playerModel: PlayerMediaEntity)
    fun playNext(playerModel: PlayerMediaEntity, skipType: SkipType)
    fun play(playerModel: PlayerMediaEntity)

    fun seekTo(millis: Long)

    fun resume()
    fun pause(stopService: Boolean, releaseFocus: Boolean = true)

    fun isPlaying(): Boolean
    fun getBookmark(): Long

    fun forwardTenSeconds()
    fun replayTenSeconds()

    fun forwardThirtySeconds()
    fun replayThirtySeconds()

    fun stopService()

    fun setVolume(volume: Float)
}

