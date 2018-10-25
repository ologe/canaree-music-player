package dev.olog.msc.presentation.popup.podcastalbum

import android.view.View
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.Podcast
import dev.olog.msc.domain.entity.PodcastAlbum
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener

class PodcastAlbumPopup(
        view: View,
        album: PodcastAlbum,
        song: Podcast?,
        listener: AbsPopupListener

) : AbsPopup(view)  {

    init {
        if (song == null){
            inflate(R.menu.dialog_podcast_album)
        } else {
            inflate(R.menu.dialog_podcast)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (song == null){
            if (album.artist == AppConstants.UNKNOWN){
                menu.removeItem(R.id.viewArtist)
            }
        } else {
            menu.removeItem(R.id.viewAlbum)

            if (song.artist == AppConstants.UNKNOWN){
                menu.removeItem(R.id.viewArtist)
            }
        }
    }

}