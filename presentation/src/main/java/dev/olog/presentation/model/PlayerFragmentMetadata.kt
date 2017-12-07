package dev.olog.presentation.model

import android.support.v4.media.MediaMetadataCompat

data class PlayerFragmentMetadata (
        val title: String,
        val artist: String,
        val isExplicit: Boolean,
        val isRemix: Boolean
)

fun MediaMetadataCompat.toPlayerMetadata(): PlayerFragmentMetadata{
    return PlayerFragmentMetadata(
            this.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
            this.getString(MediaMetadataCompat.METADATA_KEY_ARTIST),
            this.getLong(dev.olog.shared.constants.MetadataConstants.IS_EXPLICIT) != 0L,
            this.getLong(dev.olog.shared.constants.MetadataConstants.IS_REMIX) != 0L
    )
}
