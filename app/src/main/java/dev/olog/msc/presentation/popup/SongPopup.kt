package dev.olog.msc.presentation.popup

import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.Song

class SongPopup(
        view: View,
        private val song: Song

) : PopupMenu(view.context, view, Gravity.BOTTOM or Gravity.END),
        PopupMenu.OnMenuItemClickListener {

    init {
        inflate(R.menu.dialog_song)
        setOnMenuItemClickListener(this)
        if (song.artist == AppConstants.UNKNOWN){
            menu.removeItem(R.id.viewArtist)
        }
        if (song.album == AppConstants.UNKNOWN){
            menu.removeItem(R.id.viewAlbum)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

    }

}