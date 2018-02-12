package dev.olog.msc.presentation.popup

import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import dev.olog.msc.R
import dev.olog.msc.constants.PlaylistConstants
import dev.olog.msc.domain.entity.Playlist

class PlaylistPopup(
        view: View,
        private val playlist: Playlist

) : PopupMenu(view.context, view, Gravity.BOTTOM or Gravity.END),
        PopupMenu.OnMenuItemClickListener {

    init {
        inflate(R.menu.dialog_playlist)
        setOnMenuItemClickListener(this)
        if (PlaylistConstants.isAutoPlaylist(playlist.id)){
            menu.removeItem(R.id.rename)
            menu.removeItem(R.id.delete)
        }
        if (playlist.id == PlaylistConstants.LAST_ADDED_ID){
            menu.removeItem(R.id.clear)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

    }

}