package dev.olog.msc.music.service.player

interface CustomExoPlayer {

    fun prepare(songId: Long, bookmark: Long)

    fun play(songId: Long, hasFocus: Boolean, isTrackEnded: Boolean)

    fun seekTo(where: Long)

    fun resume()

    fun pause()

    fun isPlaying(): Boolean

    fun getBookmark(): Long

    fun getDuration(): Long

    fun setVolume(volume: Float)

}