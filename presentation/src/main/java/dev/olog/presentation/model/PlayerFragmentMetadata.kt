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
            this.getLong("explicit") != 0L,
            this.getLong("remix") != 0L
    )
}
