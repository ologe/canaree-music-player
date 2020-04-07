package dev.olog.presentation.popup.folder

import android.view.View
import dev.olog.domain.entity.track.Folder
import dev.olog.domain.entity.track.Song
import dev.olog.presentation.R
import dev.olog.presentation.popup.AbsPopup
import dev.olog.presentation.popup.AbsPopupListener
import dev.olog.core.isQ

internal class FolderPopup(
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

        if (isQ() && song == null) {
            // works bad on Q
            menu.removeItem(R.id.delete)
        }
    }
}