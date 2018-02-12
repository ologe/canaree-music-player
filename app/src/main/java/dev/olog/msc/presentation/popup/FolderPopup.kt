package dev.olog.msc.presentation.popup

import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import dev.olog.msc.R
import dev.olog.msc.domain.entity.Folder

class FolderPopup (
        view: View,
        private val folder: Folder

) : PopupMenu(view.context, view, Gravity.BOTTOM or Gravity.END),
        PopupMenu.OnMenuItemClickListener {


    init {
        inflate(R.menu.dialog_folder)
        setOnMenuItemClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

    }
}