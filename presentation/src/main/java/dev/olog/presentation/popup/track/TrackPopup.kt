package dev.olog.presentation.popup.track

import android.view.View
import dev.olog.core.entity.track.Track
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener

class TrackPopup(
    view: View,
    listener: AbsPopupListener,
    private val track: Track,
) : AbsPopup(view) {

    init {
        inflate(R.menu.dialog_song)

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        setupViewInfo(view) {
            listOf(track)
        }
    }

}