package dev.olog.presentation.popup.genre

import android.view.View
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Song
import dev.olog.intents.AppConstants
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener

class GenrePopup(
    view: View,
    @Suppress("UNUSED_PARAMETER") genre: Genre,
    song: Song?,
    listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        if (song == null) {
            inflate(R.menu.dialog_genre)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (song != null) {
            if (song.artist == AppConstants.UNKNOWN) {
                menu.removeItem(R.id.viewArtist)
            }
            if (song.album == AppConstants.UNKNOWN) {
                menu.removeItem(R.id.viewAlbum)
            }
        }
    }

}