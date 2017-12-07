package dev.olog.presentation.model

import android.content.res.Resources
import android.support.v4.media.MediaMetadataCompat
import dev.olog.presentation.R

data class PlayerFragmentMetadata (
        val title: String,
        val artist: String,
        val isExplicit: Boolean,
        val isRemix: Boolean
)

fun MediaMetadataCompat.toPlayerMetadata(resources: Resources): PlayerFragmentMetadata{
    val metadataArtist = this.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
    val artist = if (metadataArtist == "<unknown>") {
        resources.getString(R.string.unknown_artist)
    }  else metadataArtist
    return PlayerFragmentMetadata(
            this.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
            artist,
            this.getLong(dev.olog.shared.constants.MetadataConstants.IS_EXPLICIT) != 0L,
            this.getLong(dev.olog.shared.constants.MetadataConstants.IS_REMIX) != 0L
    )
}
