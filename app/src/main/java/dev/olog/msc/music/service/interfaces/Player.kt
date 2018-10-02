package dev.olog.msc.music.service.interfaces

import dev.olog.msc.music.service.model.PlayerMediaEntity

interface Player : PlayerLifecycle {

    fun isPlaying(): Boolean
    fun getBookmark(): Long

    fun prepare(playerModel: PlayerMediaEntity)
    fun playNext(playerModel: PlayerMediaEntity, skipType: SkipType)
    fun play(playerModel: PlayerMediaEntity)

    fun resume()
    fun pause(stopService: Boolean, releaseFocus: Boolean = true)
    fun seekTo(millis: Long)

    fun forwardTenSeconds()
    fun replayTenSeconds()

    fun stopService()

    fun setVolume(volume: Float)
}

enum class SkipType {
    NONE,
    SKIP_PREVIOUS,
    SKIP_NEXT,
    TRACK_ENDED
}
