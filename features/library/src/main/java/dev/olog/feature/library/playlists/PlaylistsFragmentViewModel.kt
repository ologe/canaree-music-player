package dev.olog.feature.library.playlists

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.track.Playlist
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.feature.library.R
import dev.olog.feature.library.tab.TabFragmentHeaders
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.feature.presentation.base.model.presentationId
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// TODO podcast playlist ??
internal class PlaylistsFragmentViewModel @Inject constructor(
    private val context: Context,
    private val headers: TabFragmentHeaders,
    private val playlistGateway: PlaylistGateway
) : ViewModel() {

    val data: Flow<List<DisplayableItem>>
        get() {
            return playlistGateway.observeAll().map { list ->
                list.map { it.toTabDisplayableItem(context.resources) }
                    .startWithIfNotEmpty(headers.playlistHeader)
            }
        }

    private fun Playlist.toTabDisplayableItem(resources: Resources): DisplayableItem {
        val layoutId = if (isPodcast) R.layout.item_playlist_podcast else R.layout.item_playlist

        return DisplayableAlbum(
            type = layoutId,
            mediaId = presentationId,
            title = title,
            subtitle = DisplayableAlbum.readableSongCount(resources, size)
        )
    }

}