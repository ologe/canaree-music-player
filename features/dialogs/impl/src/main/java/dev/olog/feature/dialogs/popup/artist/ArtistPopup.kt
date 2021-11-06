package dev.olog.feature.dialogs.popup.artist

import android.view.View
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.feature.dialogs.R
import dev.olog.feature.dialogs.popup.AbsPopup
import dev.olog.feature.dialogs.popup.AbsPopupListener
import dev.olog.shared.android.utils.isQ

class ArtistPopup(
    view: View,
    @Suppress("UNUSED_PARAMETER") artist: Artist,
    song: Song?,
    listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        if (song == null) {
            inflate(R.menu.dialog_artist)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (isQ() && song == null) {
            // works bad on Q
            menu.removeItem(R.id.delete)
        }
    }

}