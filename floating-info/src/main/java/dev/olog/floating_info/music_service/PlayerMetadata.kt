package dev.olog.floating_info.music_service

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import dev.olog.floating_info.R
import dev.olog.shared_android.TextUtils

class PlayerMetadata(
        private val context: Context,
        metadata: MediaMetadataCompat
) {

    private val title = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
    private val artist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)

    fun get(): String {
        if (context.getString(R.string.unknown_artist) == artist){
            return title
        }
        return title + TextUtils.MIDDLE_DOT_SPACED + artist
    }

}