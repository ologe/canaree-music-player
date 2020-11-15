package dev.olog.presentation.recentlyadded

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.core.interactor.GetItemTitleUseCase
import dev.olog.core.interactor.ObserveRecentlyAddedUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.recentlyadded.RecentlyAddedFragment.Companion.ARGUMENTS_MEDIA_ID
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

internal class RecentlyAddedFragmentViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    useCase: ObserveRecentlyAddedUseCase,
    getItemTitleUseCase: GetItemTitleUseCase

) : ViewModel() {

    private val mediaId = state.argument(ARGUMENTS_MEDIA_ID, MediaId::fromString)

    val itemOrdinal = mediaId.category.ordinal

    private val dataPublisher = MutableStateFlow<List<DisplayableItem>>(emptyList())
    private val titlePublisher = MutableStateFlow("")

    init {
        useCase(mediaId)
            .mapListItem { it.toRecentDetailDisplayableItem() }
            .flowOn(Dispatchers.IO)
            .onEach { dataPublisher.value = it }
            .launchIn(viewModelScope)

        getItemTitleUseCase(mediaId)
            .flowOn(Dispatchers.IO)
            .onEach { titlePublisher.value = it }
            .launchIn(viewModelScope)
    }

    fun observeData(): Flow<List<DisplayableItem>> = dataPublisher
    fun observeTitle(): Flow<String> = titlePublisher

    private fun Song.toRecentDetailDisplayableItem(): DisplayableItem {
        return DisplayableTrack(
            type = R.layout.item_recently_added,
            mediaId = MediaId.playableItem(mediaId, id),
            title = title,
            artist = artist,
            album = album,
            idInPlaylist = idInPlaylist,
            dataModified = this.dateModified
        )
    }


}
