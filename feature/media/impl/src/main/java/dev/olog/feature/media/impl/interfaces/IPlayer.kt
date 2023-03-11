package dev.olog.feature.media.impl.interfaces

import dev.olog.feature.media.impl.model.PlayerMediaEntity
import dev.olog.feature.media.impl.model.SkipType

interface IPlayer : IPlayerLifecycle {

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

