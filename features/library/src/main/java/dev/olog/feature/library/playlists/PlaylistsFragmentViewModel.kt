package dev.olog.feature.library.playlists

import android.content.Context
import androidx.lifecycle.ViewModel
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.feature.library.tab.TabFragmentHeaders
import dev.olog.feature.library.tab.toTabDisplayableItem
import dev.olog.feature.presentation.base.model.DisplayableItem
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

}