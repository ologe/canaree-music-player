package dev.olog.presentation.model

import android.support.v4.media.MediaMetadataCompat
import dev.olog.shared.TextUtils

data class PlayerFragmentMetadata (
        val title: String,
        val artist: String,
        val isExplicit: Boolean,
        val isRemix: Boolean
) {

    companion object {
        const val KEY_EXPLICIT = "KEY_EXPLICIT"
        const val KEY_REMIX = "KEY_REMIX"
    }

}

fun MediaMetadataCompat.toPlayerMetadata(): PlayerFragmentMetadata{
    return PlayerFragmentMetadata(
            this.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
            this.getString(MediaMetadataCompat.METADATA_KEY_ALBUM) +
                    TextUtils.MIDDLE_DOT_SPACED +
                    this.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
            false,
            false
    )
}
