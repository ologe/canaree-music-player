package dev.olog.service.music.interfaces

import dev.olog.service.music.model.PlayerMediaEntity
import kotlin.time.Duration

internal interface IPlayer {

    fun prepare(playerModel: PlayerMediaEntity, forcePause: Boolean)

    fun seekTo(millis: Duration)

    fun resume()
    fun pause(stopService: Boolean, releaseFocus: Boolean = true)

    fun isPlaying(): Boolean
    fun getBookmark(): Duration

    fun forwardTenSeconds()
    fun replayTenSeconds()

    fun forwardThirtySeconds()
    fun replayThirtySeconds()

    fun stopService()

    fun setVolume(volume: Float)

    fun setDucking(enabled: Boolean)
}

