package dev.olog.msc.presentation.model

import android.support.v4.media.MediaMetadataCompat
import dev.olog.msc.constants.MetadataConstants
import dev.olog.shared_android.Constants

data class PlayerFragmentMetadata (
        val title: String,
        val artist: String,
        val isExplicit: Boolean,
        val isRemix: Boolean
)

fun MediaMetadataCompat.toPlayerMetadata(): PlayerFragmentMetadata{
    var artist = this.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
    if (artist == Constants.UNKNOWN){
        artist = Constants.UNKNOWN_ARTIST
    }

    return PlayerFragmentMetadata(
            this.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
            artist,
            this.getLong(MetadataConstants.IS_EXPLICIT) != 0L,
            this.getLong(MetadataConstants.IS_REMIX) != 0L
    )
}
