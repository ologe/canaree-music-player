package dev.olog.msc.presentation.popup

import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import dev.olog.msc.R
import dev.olog.msc.domain.entity.Artist

class ArtistPopup (
        view: View,
        private val artist: Artist

) : PopupMenu(view.context, view, Gravity.BOTTOM or Gravity.END),
        PopupMenu.OnMenuItemClickListener {

    init {
        inflate(R.menu.dialog_artist)
        setOnMenuItemClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

    }

}