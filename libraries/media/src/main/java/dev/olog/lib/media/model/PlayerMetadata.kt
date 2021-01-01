package dev.olog.lib.media.model

import android.support.v4.media.MediaMetadataCompat
import dev.olog.domain.mediaid.MediaId
import dev.olog.lib.media.MusicConstants
import dev.olog.lib.media.getBoolean
import dev.olog.shared.android.TextUtils
import kotlin.time.Duration
import kotlin.time.milliseconds

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
        get() = metadata.getText(MediaMetadataCompat.METADATA_KEY_TITLE).toString()

    val artist: String
        get() = metadata.getText(MediaMetadataCompat.METADATA_KEY_ARTIST).toString()

    val album: String
        get() = metadata.getText(MediaMetadataCompat.METADATA_KEY_ALBUM).toString()

    val duration: Duration
        get() = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).milliseconds

    val isPodcast: Boolean
        get() = metadata.getBoolean(MusicConstants.IS_PODCAST)

    val isSkippingToNext: Boolean
        get() = metadata.getBoolean(MusicConstants.SKIP_NEXT)
    val isSkippingToPrevious: Boolean
        get() = metadata.getBoolean(MusicConstants.SKIP_PREVIOUS)

    val readableDuration: String
        get() = TextUtils.formatTimeMillis(duration)



    override fun equals(other: Any?): Boolean {
        if (other == null || other !is PlayerMetadata) {
            return false
        }
        if (this === other) {
            return true
        }

        return this.mediaId == other.mediaId &&
            this.isSkippingToNext == other.isSkippingToNext &&
            this.isSkippingToPrevious == other.isSkippingToPrevious
    }

    override fun hashCode(): Int {
        var result = mediaId.hashCode()
        result = 31 * result + isSkippingToNext.hashCode()
        result = 31 * result + isSkippingToPrevious.hashCode()
        return result
    }


}