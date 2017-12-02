package dev.olog.presentation.dialog_entry

import android.widget.PopupMenu
import dagger.Lazy
import dev.olog.shared.MediaIdHelper
import javax.inject.Inject

class MenuListenerFactory @Inject constructor(
        private val baseMenuListener: Lazy<BaseMenuListener>,
        private val albumMenuListener: Lazy<AlbumMenuListener>,
        private val playlistMenuListener: Lazy<PlaylistMenuListener>,
        private val songMenuListener: Lazy<SongMenuListener>
) {

    fun get(mediaId: String): PopupMenu.OnMenuItemClickListener {
        return when (MediaIdHelper.extractCategory(mediaId)){
            MediaIdHelper.MEDIA_ID_BY_ALL -> songMenuListener.get().setMediaId(mediaId)
            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> playlistMenuListener.get().setMediaId(mediaId)
            MediaIdHelper.MEDIA_ID_BY_ALBUM -> albumMenuListener.get().setMediaId(mediaId)
            else -> baseMenuListener.get().setMediaId(mediaId)
        }
    }

}