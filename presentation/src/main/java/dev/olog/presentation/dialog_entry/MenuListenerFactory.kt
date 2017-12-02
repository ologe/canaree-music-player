package dev.olog.presentation.dialog_entry

import android.widget.PopupMenu
import dagger.Lazy
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper
import javax.inject.Inject

class MenuListenerFactory @Inject constructor(
        private val baseMenuListener: Lazy<BaseMenuListener>,
        private val albumMenuListener: Lazy<AlbumMenuListener>,
        private val playlistMenuListener: Lazy<PlaylistMenuListener>,
        private val songMenuListener: Lazy<SongMenuListener>
) {

    fun get(item: DisplayableItem): PopupMenu.OnMenuItemClickListener {
        return when (MediaIdHelper.extractCategory(item.mediaId)){
            MediaIdHelper.MEDIA_ID_BY_ALL -> songMenuListener.get().setMediaId(item)
            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> playlistMenuListener.get().setMediaId(item)
            MediaIdHelper.MEDIA_ID_BY_ALBUM -> albumMenuListener.get().setMediaId(item)
            else -> baseMenuListener.get().setMediaId(item)
        }
    }

}