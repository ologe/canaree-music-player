package dev.olog.presentation.playlist.chooser

import android.content.Context
import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.track.Playlist
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class PlaylistChooserActivityViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    playlistGateway: PlaylistGateway
) : ViewModel() {

    private val data = MutableStateFlow<List<DisplayableItem>>(emptyList())

    init {
        playlistGateway.observeAll()
            .mapListItem { it.toDisplayableItem(context.resources) }
            .flowOn(Dispatchers.IO)
            .onEach { data.value = it }
            .launchIn(viewModelScope)
    }

    fun observeData(): Flow<List<DisplayableItem>> = data

    private fun Playlist.toDisplayableItem(resources: Resources): DisplayableItem {
        return DisplayableAlbum(
            type = R.layout.item_playlist_chooser,
            mediaId = getMediaId(),
            title = title,
            subtitle = DisplayableAlbum.readableSongCount(resources, size)
        )
    }

}