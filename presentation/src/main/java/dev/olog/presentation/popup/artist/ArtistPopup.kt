package dev.olog.presentation.popup.artist

import android.view.View
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.platform.BuildVersion
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener

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

        if (BuildVersion.isQ() && song == null) {
            // works bad on Q
            menu.removeItem(R.id.delete)
        }
    }

}