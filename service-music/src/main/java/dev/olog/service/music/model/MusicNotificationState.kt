package dev.olog.service.music.model

import android.support.v4.media.session.PlaybackStateCompat
import java.util.concurrent.TimeUnit

internal sealed class Event {

    class Metadata(
        @JvmField
        val entity: MediaEntity
    ) : Event()

    class State(
        @JvmField
        val state: PlaybackStateCompat
    ) : Event()

    class Favorite(
        @JvmField
        val favorite: Boolean
    ) : Event()

}

/**
 * Used to sync 3 different data sources,
 * metadata, state and favorite
 */
internal class MusicNotificationState(
    @JvmField
    var id: Long = -1,
    @JvmField
    var title: String = "",
    @JvmField
    var artist: String = "",
    @JvmField
    var album: String = "",
    @JvmField
    var isPlaying: Boolean = false,
    @JvmField
    var bookmark: Long = -1,
    @JvmField
    var duration: Long = -1,
    @JvmField
    var isFavorite: Boolean = false,
    @JvmField
    var isPodcast: Boolean = false
) {

    /**
     * @return true if contains the minimal state for begin posted as a notification
     */
    private fun isValidState(): Boolean {
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
        this.duration = metadata.duration
        this.isPodcast = metadata.isPodcast
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
                this.isPodcast != metadata.isPodcast
    }

    fun isDifferentState(state: PlaybackStateCompat): Boolean {
        val isPlaying = state.state == PlaybackStateCompat.STATE_PLAYING
        val bookmark = TimeUnit.MILLISECONDS.toSeconds(state.position)
        return this.isPlaying != isPlaying ||
                TimeUnit.MILLISECONDS.toSeconds(this.bookmark) != bookmark
    }

    fun isDifferentFavorite(isFavorite: Boolean): Boolean {
        return this.isFavorite != isFavorite
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MusicNotificationState

        if (id != other.id) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (album != other.album) return false
        if (isPlaying != other.isPlaying) return false
        if (bookmark != other.bookmark) return false
        if (duration != other.duration) return false
        if (isFavorite != other.isFavorite) return false
        if (isPodcast != other.isPodcast) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + isPlaying.hashCode()
        result = 31 * result + bookmark.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + isFavorite.hashCode()
        result = 31 * result + isPodcast.hashCode()
        return result
    }

    fun deepCopy(): MusicNotificationState {
        return MusicNotificationState(
            id, title, artist, album, isPlaying, bookmark, duration, isFavorite, isPodcast
        )
    }

}