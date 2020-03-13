package dev.olog.media.model

import android.support.v4.media.MediaMetadataCompat
import dev.olog.core.MediaId
import dev.olog.intents.MusicConstants
import dev.olog.shared.TextUtils
import dev.olog.shared.android.extensions.getBoolean

class PlayerMetadata(
    private val metadata: MediaMetadataCompat
) {

    val id: Long
        get() = mediaId.id

    val mediaId: MediaId.Track
        get() {
            val mediaId = metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
            return MediaId.fromString(mediaId) as MediaId.Track
        }

    val title: String
        get() = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)

    val artist: String
        get() = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)

    val album: String
        get() = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)

    val duration: Long
        get() = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)

    val isPodcast: Boolean
        get() = metadata.getBoolean(MusicConstants.IS_PODCAST)

    val path: String
        get() = metadata.getString(MusicConstants.PATH)

    val isSkippingToNext
        get() = metadata.getBoolean(MusicConstants.SKIP_NEXT)

    val isSkippingToPrevious
        get() = metadata.getBoolean(MusicConstants.SKIP_PREVIOUS)

    val readableDuration: String
        get() = TextUtils.formatMillis(duration)

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