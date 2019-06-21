package dev.olog.msc.presentation.popup.folder

import android.view.View
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.core.entity.Folder
import dev.olog.core.entity.Song
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener

@Suppress("UNUSED_PARAMETER")
class FolderPopup (
    view: View,
    folder: Folder,
    song: Song?,
    listener: AbsPopupListener

) : AbsPopup(view) {


    init {
        if (song == null){
            inflate(R.menu.dialog_folder)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (song != null){
            if (song.artist == AppConstants.UNKNOWN){
                menu.removeItem(R.id.viewArtist)
            }
            if (song.album == AppConstants.UNKNOWN){
                menu.removeItem(R.id.viewAlbum)
            }
        }

    }
}