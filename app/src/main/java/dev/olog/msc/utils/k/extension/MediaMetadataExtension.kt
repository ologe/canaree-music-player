package dev.olog.msc.utils.k.extension

import android.support.v4.media.MediaMetadataCompat
import dev.olog.msc.constants.MusicConstants
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.TextUtils

fun MediaMetadataCompat.getTitle(): CharSequence {
    return getText(MediaMetadataCompat.METADATA_KEY_TITLE)
}

fun MediaMetadataCompat.getArtist(): CharSequence {
    val artist = getText(MediaMetadataCompat.METADATA_KEY_ARTIST)
    return DisplayableItem.adjustArtist(artist.toString())
}

fun MediaMetadataCompat.getDuration(): Long {
    return getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
}

fun MediaMetadataCompat.getDurationReadable(): String {
    val duration = getDuration()
    return TextUtils.MIDDLE_DOT_SPACED + TextUtils.formatMillis(duration)
}

fun MediaMetadataCompat.isRemix(): Boolean {
    return getLong(MusicConstants.IS_REMIX) == 1L
}

fun MediaMetadataCompat.isExplicit(): Boolean {
    return getLong(MusicConstants.IS_EXPLICIT) == 1L
}

fun MediaMetadataCompat.getImage(): String {
    return getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
}

fun MediaMetadataCompat.getMediaId(): MediaId {
    val mediaId = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
    return MediaId.fromString(mediaId)
}

fun MediaMetadataCompat.getId(): Long {
    return getMediaId().leaf!!
}