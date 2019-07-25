package dev.olog.msc.presentation.shortcuts.playlist.chooser

import android.content.res.Resources
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.getMediaId
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.msc.R
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.android.extensions.mapToList
import io.reactivex.Observable
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class PlaylistChooserActivityViewPresenter @Inject constructor(
    private val getAllPlaylistsUseCase: PlaylistGateway
) {

    fun execute(resources: Resources): Observable<List<DisplayableItem>> {
        return getAllPlaylistsUseCase.observeAll().asObservable()
            .mapToList { it.toDisplayableItem(resources) }
    }

    private fun Playlist.toDisplayableItem(resources: Resources): DisplayableItem {
        return DisplayableAlbum(
            type = R.layout.item_tab_album,
            mediaId = getMediaId(),
            title = title,
            subtitle = DisplayableAlbum.readableSongCount(resources, size)
        )
    }

}