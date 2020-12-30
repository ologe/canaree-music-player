package dev.olog.feature.dialog.popup.folder

import android.view.View
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Track
import dev.olog.feature.dialog.R
import dev.olog.feature.dialog.popup.AbsPopup
import dev.olog.feature.dialog.popup.AbsPopupListener
import dev.olog.shared.android.utils.isQ

class FolderPopup(
    view: View,
    @Suppress("UNUSED_PARAMETER") folder: Folder,
    track: Track?,
    listener: AbsPopupListener

) : AbsPopup(view) {


    init {
        if (track == null) {
            inflate(R.menu.dialog_folder)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (isQ() && track == null) {
            // works bad on Q
            menu.removeItem(R.id.delete)
        }
    }
}