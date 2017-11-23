package dev.olog.presentation.fragment_mini_player.model

import android.support.v4.media.MediaMetadataCompat
import dev.olog.shared.TextUtils

data class MiniPlayerMedatata(
        val title: String,
        val subtitle: String
)

fun MediaMetadataCompat.toMiniPlayerMetadata() : MiniPlayerMedatata{
    return MiniPlayerMedatata(
            this.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
            this.getString(MediaMetadataCompat.METADATA_KEY_ARTIST) +
                    TextUtils.MIDDLE_DOT_SPACED +
                    this.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
    )
}
