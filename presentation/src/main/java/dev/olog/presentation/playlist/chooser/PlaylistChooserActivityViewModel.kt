package dev.olog.presentation.playlist.chooser

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.entity.track.Playlist
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.ApplicationContext
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class PlaylistChooserActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val playlistGateway: PlaylistGateway,
    private val schedulers: Schedulers
) : ViewModel() {

    private val data = MutableLiveData<List<DisplayableItem>>()

    init {
        playlistGateway.observeAll()
            .mapListItem { it.toDisplayableItem(context.resources) }
            .flowOn(schedulers.io)
            .onEach { data.value = it }
            .launchIn(viewModelScope)
    }

    fun observeData(): LiveData<List<DisplayableItem>> = data

    private fun Playlist.toDisplayableItem(resources: Resources): DisplayableItem {
        return DisplayableAlbum(
            type = R.layout.item_playlist_chooser,
            mediaId = getMediaId(),
            title = title,
            subtitle = DisplayableAlbum.readableSongCount(resources, size)
        )
    }

}