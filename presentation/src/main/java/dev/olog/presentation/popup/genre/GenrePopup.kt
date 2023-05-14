package dev.olog.presentation.popup.genre

import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup

class GenrePopup(
    view: View,
    listener: GenrePopupListener

) : AbsPopup(view) {

    init {
        inflate(R.menu.dialog_genre)
        addPlaylistChooser(view.context, listener.getPlaylists(false))
        setOnMenuItemClickListener(listener)
    }

}