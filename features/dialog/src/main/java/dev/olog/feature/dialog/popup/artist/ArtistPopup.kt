package dev.olog.feature.dialog.popup.artist

import android.view.View
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Track
import dev.olog.feature.dialog.R
import dev.olog.feature.dialog.popup.AbsPopup
import dev.olog.feature.dialog.popup.AbsPopupListener
import dev.olog.shared.android.utils.isQ

class ArtistPopup(
    view: View,
    @Suppress("UNUSED_PARAMETER") artist: Artist,
    track: Track?,
    listener: AbsPopupListener,
    tracks: suspend () -> List<Track>
) : AbsPopup(view) {

    init {
        if (track == null) {
            inflate(R.menu.dialog_artist)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (isQ() && track == null) {
            // works bad on Q
            menu.removeItem(R.id.delete)
        }

        setupViewInfo(view, tracks)
    }

}