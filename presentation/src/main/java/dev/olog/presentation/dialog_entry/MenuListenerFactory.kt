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
        val mediaId = item.mediaId
        val isSong = MediaIdHelper.isSong(mediaId)
        val category = MediaIdHelper.extractCategory(mediaId)

        return when {
            category == MediaIdHelper.MEDIA_ID_BY_ALL || isSong -> songMenuListener.get().setMediaId(item)
            category == MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> playlistMenuListener.get().setMediaId(item)
            category == MediaIdHelper.MEDIA_ID_BY_ALBUM -> albumMenuListener.get().setMediaId(item)
            else -> baseMenuListener.get().setMediaId(item)
        }
    }

}