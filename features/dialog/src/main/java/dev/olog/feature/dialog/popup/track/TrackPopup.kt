package dev.olog.feature.dialog.popup.track

import android.view.View
import dev.olog.domain.entity.track.Track
import dev.olog.feature.dialog.R
import dev.olog.feature.dialog.popup.AbsPopup
import dev.olog.feature.dialog.popup.AbsPopupListener

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