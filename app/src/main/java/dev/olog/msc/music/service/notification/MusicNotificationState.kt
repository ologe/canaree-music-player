package dev.olog.msc.music.service.notification

import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.msc.BuildConfig
import dev.olog.msc.music.service.model.MediaEntity
import java.util.concurrent.TimeUnit

data class MusicNotificationState (
        var id: Long = -1,
        var title: String = "",
        var artist: String = "",
        var album: String = "",
        var image: String = "",
        var isPlaying: Boolean = false,
        var bookmark: Long = -1,
        var duration: Long = -1,
        var isFavorite: Boolean = false
) {

    private fun isValidState(): Boolean{
        if (BuildConfig.DEBUG){
            println(id)
            println(title)
            println(artist)
            println(album)
            println(image)
            println(bookmark)
            println(duration)

        }


        return id != -1L &&
                title.isNotBlank() &&
                artist.isNotBlank() &&
                album.isNotBlank() &&
//                image.isNotBlank() &&
                bookmark != -1L &&
                duration != -1L
    }

    fun updateMetadata(metadata: MediaEntity): Boolean {
        this.id = metadata.id
        this.title = metadata.title
        this.artist = metadata.artist
        this.album = metadata.album
        this.image = metadata.image
        this.duration = metadata.duration
        return isValidState()
    }

    fun updateState(state: PlaybackStateCompat): Boolean {
        this.isPlaying = state.state == PlaybackStateCompat.STATE_PLAYING
        this.bookmark = state.position
        return isValidState()
    }

    fun updateFavorite(isFavorite: Boolean): Boolean {
        this.isFavorite = isFavorite
        return isValidState()
    }

    fun isDifferentMetadata(metadata: MediaEntity): Boolean {
        return this.id != metadata.id ||
                this.title != metadata.title ||
                this.artist != metadata.artist ||
                this.album != metadata.album ||
                this.image != metadata.image
    }

    fun isDifferentState(state: PlaybackStateCompat): Boolean{
        val isPlaying = state.state == PlaybackStateCompat.STATE_PLAYING
        val bookmark = TimeUnit.MILLISECONDS.toSeconds(state.position)
        return this.isPlaying != isPlaying ||
                TimeUnit.MILLISECONDS.toSeconds(this.bookmark) != bookmark
    }

    fun isDifferentFavorite(isFavorite: Boolean): Boolean {
        return this.isFavorite != isFavorite
    }

}