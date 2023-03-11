package dev.olog.feature.media.impl.interfaces

interface IPlayerDelegate<T> {

    fun prepare(mediaEntity: T, bookmark: Long)

    fun play(mediaEntity: T, hasFocus: Boolean, isTrackEnded: Boolean)

    fun seekTo(where: Long)

    fun resume()
    fun pause()

    fun isPlaying(): Boolean
    fun getBookmark(): Long

    fun getDuration(): Long

    fun setVolume(volume: Float)

    fun setPlaybackSpeed(speed: Float)

}