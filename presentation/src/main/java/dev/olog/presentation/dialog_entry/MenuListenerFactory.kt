package dev.olog.presentation.dialog_entry

import android.widget.PopupMenu
import dagger.Lazy
import dev.olog.presentation.model.DisplayableItem
import javax.inject.Inject

class MenuListenerFactory @Inject constructor(
        private val baseMenuListener: Lazy<BaseMenuListener>,
        private val albumMenuListener: Lazy<AlbumMenuListener>,
        private val playlistMenuListener: Lazy<PlaylistMenuListener>,
        private val songMenuListener: Lazy<SongMenuListener>
) {

    fun get(item: DisplayableItem): PopupMenu.OnMenuItemClickListener {
        val mediaId = item.mediaId

        return when {
            mediaId.isLeaf -> songMenuListener.get().setMediaId(item)
            mediaId.isPlaylist -> playlistMenuListener.get().setMediaId(item)
            mediaId.isAlbum -> albumMenuListener.get().setMediaId(item)
            else -> baseMenuListener.get().setMediaId(item)
        }
    }

}