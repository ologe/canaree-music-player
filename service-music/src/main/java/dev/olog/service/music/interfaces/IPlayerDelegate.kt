package dev.olog.service.music.interfaces

import kotlin.time.Duration

internal interface IPlayerDelegate<T> {

    fun prepare(model: T, isTrackEnded: Boolean)

    fun seekTo(where: Duration)

    fun resume()
    fun pause()

    fun isPlaying(): Boolean

    fun getBookmark(): Duration
    fun getDuration(): Duration

    fun setVolume(volume: Float)

    var playbackSpeed: Float

    fun setDucking(enabled: Boolean)

}