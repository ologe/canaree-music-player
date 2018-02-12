package dev.olog.msc.presentation.popup

import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.Album

class AlbumPopup(
        view: View,
        private val album: Album

) : PopupMenu(view.context, view, Gravity.BOTTOM or Gravity.END),
        PopupMenu.OnMenuItemClickListener  {

    init {
        inflate(R.menu.dialog_album)
        setOnMenuItemClickListener(this)
        if (album.artist == AppConstants.UNKNOWN){
            menu.removeItem(R.id.viewArtist)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

    }

}