package dev.olog.media

import android.support.v4.media.MediaMetadataCompat
import dev.olog.core.MediaId
import dev.olog.shared.MusicConstants
import dev.olog.shared.utils.TextUtils

fun MediaMetadataCompat.getTitle(): CharSequence {
    return getText(MediaMetadataCompat.METADATA_KEY_TITLE)
}

fun MediaMetadataCompat.getArtist(): CharSequence {
    val artist = getText(MediaMetadataCompat.METADATA_KEY_ARTIST)
    return artist.toString()
}

fun MediaMetadataCompat.getAlbum(): CharSequence {
    val album = getText(MediaMetadataCompat.METADATA_KEY_ALBUM)
    return album.toString()
}

fun MediaMetadataCompat.getDuration(): Long {
    return getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
}

fun MediaMetadataCompat.getDurationReadable(): String {
    val duration = getDuration()
    return TextUtils.formatMillis(duration)
}

fun MediaMetadataCompat.getMediaId(): MediaId {
    val mediaId = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
    return MediaId.fromString(mediaId)
}

fun MediaMetadataCompat.getId(): Long {
    return getMediaId().leaf!!
}

fun MediaMetadataCompat.isPodcast(): Boolean {
    return getBoolean(MusicConstants.IS_PODCAST)
}

fun MediaMetadataCompat.Builder.putBoolean(
    key: String,
    value: Boolean
): MediaMetadataCompat.Builder {
    putLong(key, if (value) 1 else 0)
    return this
}

fun MediaMetadataCompat.getBoolean(key: String): Boolean {
    return getLong(key) != 0L
}