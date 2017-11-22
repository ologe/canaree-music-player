package dev.olog.music_service.interfaces

import dev.olog.music_service.model.PlayerMediaEntity

interface Player {

    fun isPlaying(): Boolean
    fun getBookmark(): Long

    fun prepare(pairSongBookmark: Pair<PlayerMediaEntity, Long>)
    fun playNext(playerModel: PlayerMediaEntity, nextTo: Boolean)
    fun play(playerModel: PlayerMediaEntity)

    fun resume()
    fun pause(stopService: Boolean)
    fun seekTo(millis: Long)

    fun stopService()
}
