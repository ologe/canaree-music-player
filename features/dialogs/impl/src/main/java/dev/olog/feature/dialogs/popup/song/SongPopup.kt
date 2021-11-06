package dev.olog.feature.dialogs.popup.song

import android.view.View
import dev.olog.feature.dialogs.R
import dev.olog.feature.dialogs.popup.AbsPopup
import dev.olog.feature.dialogs.popup.AbsPopupListener

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