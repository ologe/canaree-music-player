package dev.olog.presentation.playlist.chooser

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.track.Playlist
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.presentation.R
import dev.olog.feature.base.model.DisplayableAlbum
import dev.olog.feature.base.model.DisplayableItem
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistChooserActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val playlistGateway: PlaylistGateway
) : ViewModel() {

    private val data = MutableLiveData<List<DisplayableItem>>()

    init {
        viewModelScope.launch {
            playlistGateway.observeAll()
                .mapListItem { it.toDisplayableItem(context.resources) }
                .flowOn(Dispatchers.IO)
                .collect { data.value = it }
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
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