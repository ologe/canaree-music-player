package dev.olog.feature.media.api.model

import android.support.v4.media.MediaMetadataCompat
import dev.olog.core.MediaId
import dev.olog.feature.media.api.MusicConstants
import dev.olog.shared.TextUtils

class PlayerMetadata(private val metadata: MediaMetadataCompat) {

    val id: Long = mediaId.leaf!!
    val mediaId: MediaId
        get() {
            val mediaId = metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
            return MediaId.fromString(mediaId)
        }

    val title: String = metadata.getText(MediaMetadataCompat.METADATA_KEY_TITLE).toString()
    val artist: String = metadata.getText(MediaMetadataCompat.METADATA_KEY_ARTIST).toString()
    val album: String = metadata.getText(MediaMetadataCompat.METADATA_KEY_ALBUM).toString()
    val duration: Long = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
    val isPodcast: Boolean = metadata.getBoolean(MusicConstants.IS_PODCAST)
    val path: String = metadata.getString(MusicConstants.PATH).toString()

    val isSkippingToNext = metadata.getBoolean(MusicConstants.SKIP_NEXT)
    val isSkippingToPrevious = metadata.getBoolean(MusicConstants.SKIP_PREVIOUS)

    val readableDuration: String = TextUtils.formatMillis(duration)

    private fun MediaMetadataCompat.getBoolean(key: String): Boolean {
        return getLong(key) != 0L
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is PlayerMetadata) {
            return false
        }
        if (this === other) {
            return true
        }

        return this.mediaId == other.mediaId &&
                this.path == other.path &&
                this.isSkippingToNext == other.isSkippingToNext &&
                this.isSkippingToPrevious == other.isSkippingToPrevious &&
                this.isPodcast == other.isPodcast
    }

    override fun hashCode(): Int {
        var result = metadata.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + isPodcast.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + isSkippingToNext.hashCode()
        result = 31 * result + isSkippingToPrevious.hashCode()
        return result
    }


}