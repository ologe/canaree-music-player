package dev.olog.msc.presentation.popup.album

import android.view.View
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener

class AlbumPopup(
        view: View,
        album: Album,
        song: Song?,
        listener: AbsPopupListener

) : AbsPopup(view)  {

    init {
        if (song == null){
            inflate(R.menu.dialog_album)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (album.artist == AppConstants.UNKNOWN){
            menu.removeItem(R.id.viewArtist)
        }

        song?.let {
            menu.removeItem(R.id.viewAlbum)

            if (it.artist == AppConstants.UNKNOWN){
                menu.removeItem(R.id.viewArtist)
            }
        }
    }

}