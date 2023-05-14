package dev.olog.presentation.popup.artist

import android.view.View
import dev.olog.core.entity.track.Artist
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup

class ArtistPopup(
    view: View,
    artist: Artist,
    listener: ArtistPopupListener
) : AbsPopup(view) {

    init {
        inflate(R.menu.dialog_artist)
        addPlaylistChooser(view.context, listener.getPlaylists(artist.isPodcast))
        setOnMenuItemClickListener(listener)
    }

}