package dev.olog.msc.music.service.player

import dev.olog.msc.music.service.model.MediaEntity

interface CustomExoPlayer {

    fun prepare(mediaEntity: MediaEntity, bookmark: Long)

    fun play(mediaEntity: MediaEntity, hasFocus: Boolean, isTrackEnded: Boolean)

    fun seekTo(where: Long)

    fun resume()

    fun pause()

    fun isPlaying(): Boolean

    fun getBookmark(): Long

    fun getDuration(): Long

    fun setVolume(volume: Float)

}