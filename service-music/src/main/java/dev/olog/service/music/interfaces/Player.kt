package dev.olog.service.music.interfaces

import dev.olog.service.music.model.SkipType

interface Player : PlayerLifecycle {

    fun isPlaying(): Boolean
    fun getBookmark(): Long

    fun prepare(playerModel: dev.olog.service.music.model.PlayerMediaEntity)
    fun playNext(playerModel: dev.olog.service.music.model.PlayerMediaEntity, skipType: SkipType)
    fun play(playerModel: dev.olog.service.music.model.PlayerMediaEntity)

    fun resume()
    fun pause(stopService: Boolean, releaseFocus: Boolean = true)
    fun seekTo(millis: Long)

    fun forwardTenSeconds()
    fun replayTenSeconds()

    fun forwardThirtySeconds()
    fun replayThirtySeconds()

    fun stopService()

    fun setVolume(volume: Float)
}

