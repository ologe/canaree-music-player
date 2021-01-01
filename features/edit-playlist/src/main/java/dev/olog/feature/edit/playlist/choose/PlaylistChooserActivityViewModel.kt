package dev.olog.feature.edit.playlist.choose

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

internal class PlaylistChooserActivityViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    playlistGateway: PlaylistGateway
) : ViewModel() {

    private val data = MutableStateFlow<List<PlaylistChooserActivityModel>>(emptyList())

    init {
        playlistGateway.observeAll()
            .mapListItem { it.toDisplayableItem(context.resources) }
            .flowOn(Dispatchers.IO)
            .onEach { data.value = it }
            .launchIn(viewModelScope)
    }

    fun observeData(): Flow<List<PlaylistChooserActivityModel>> = data

}