package dev.olog.msc.presentation.mini.player.model

import android.support.v4.media.MediaMetadataCompat
import dev.olog.shared_android.Constants

data class MiniPlayerMedatata(
        val title: String,
        val subtitle: String
)

fun MediaMetadataCompat.toMiniPlayerMetadata() : MiniPlayerMedatata{
    var artist = this.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
    if (artist == Constants.UNKNOWN){
       artist = Constants.UNKNOWN_ARTIST
    }

    return MiniPlayerMedatata(
            this.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
            artist
    )
}
