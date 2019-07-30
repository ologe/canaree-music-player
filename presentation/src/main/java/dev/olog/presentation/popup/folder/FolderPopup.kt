package dev.olog.presentation.popup.folder

import android.view.View
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song
import dev.olog.intents.AppConstants
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener

class FolderPopup(
    view: View,
    @Suppress("UNUSED_PARAMETER") folder: Folder,
    song: Song?,
    listener: AbsPopupListener

) : AbsPopup(view) {


    init {
        if (song == null) {
            inflate(R.menu.dialog_folder)
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