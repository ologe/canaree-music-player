package dev.olog.presentation.popup.track

import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener

class SongPopup(
    view: View,
    listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        inflate(R.menu.dialog_song)

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)
    }

}