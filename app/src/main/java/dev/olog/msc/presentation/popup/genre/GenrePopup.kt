package dev.olog.msc.presentation.popup.genre

import android.view.View
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.Genre
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener

class GenrePopup(
        view: View,
        genre: Genre,
        song: Song?,
        listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        if (song == null){
            inflate(R.menu.dialog_genre)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        song?.let {
            if (it.artist == AppConstants.UNKNOWN){
                menu.removeItem(R.id.viewArtist)
            }
            if (it.album == AppConstants.UNKNOWN){
                menu.removeItem(R.id.viewAlbum)
            }
        }
    }

}