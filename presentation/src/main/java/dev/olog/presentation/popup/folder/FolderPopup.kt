package dev.olog.presentation.popup.folder

import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup

class FolderPopup(
    view: View,
    listener: FolderPopupListener

) : AbsPopup(view) {


    init {
        inflate(R.menu.dialog_folder)
        addPlaylistChooser(view.context, listener.getPlaylists(false))
        setOnMenuItemClickListener(listener)
    }
}