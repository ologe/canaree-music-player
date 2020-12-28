package dev.olog.presentation.popup.album

import android.view.View
import dev.olog.core.entity.track.Track
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener
import dev.olog.shared.android.utils.isQ

class AlbumPopup(
    view: View,
    track: Track?,
    listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        if (track == null) {
            inflate(R.menu.dialog_album)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (isQ() && track == null) {
            // works bad on Q
            menu.removeItem(R.id.delete)
        }
    }

}