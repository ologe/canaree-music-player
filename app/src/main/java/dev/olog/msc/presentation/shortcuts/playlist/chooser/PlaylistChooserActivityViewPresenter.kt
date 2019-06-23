package dev.olog.msc.presentation.shortcuts.playlist.chooser

import android.content.res.Resources
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Playlist
import dev.olog.msc.R
import dev.olog.msc.domain.interactor.all.GetAllPlaylistsUseCase
import dev.olog.shared.mapToList
import dev.olog.presentation.model.DisplayableItem
import io.reactivex.Observable
import javax.inject.Inject

class PlaylistChooserActivityViewPresenter @Inject constructor(
        private val getAllPlaylistsUseCase: GetAllPlaylistsUseCase
) {

    fun execute(resources: Resources): Observable<List<DisplayableItem>> {
        return getAllPlaylistsUseCase.execute().mapToList { it.toDisplayableItem(resources) }
    }

    private fun Playlist.toDisplayableItem(resources: Resources): DisplayableItem {
        val size = DisplayableItem.handleSongListSize(resources, size)

        return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.playlistId(id),
            title,
            size
        )
    }

}