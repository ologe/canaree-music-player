package dev.olog.service.music.interfaces

internal interface IPlayerDelegate<T> {

    fun prepare(model: T, isTrackEnded: Boolean)

    fun seekTo(where: Long)

    fun resume()
    fun pause()

    fun isPlaying(): Boolean
    fun getBookmark(): Long

    fun getDuration(): Long

    fun setVolume(volume: Float)

    var playbackSpeed: Float

}