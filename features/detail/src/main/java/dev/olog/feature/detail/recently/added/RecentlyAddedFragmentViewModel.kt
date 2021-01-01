package dev.olog.feature.detail.recently.added

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.entity.track.Track
import dev.olog.domain.interactor.GetItemTitleUseCase
import dev.olog.domain.interactor.ObserveRecentlyAddedUseCase
import dev.olog.navigation.Params
import dev.olog.shared.android.DisplayableItemUtils
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

internal class RecentlyAddedFragmentViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    useCase: ObserveRecentlyAddedUseCase,
    getItemTitleUseCase: GetItemTitleUseCase

) : ViewModel() {

    private val mediaId = state.argument(Params.MEDIA_ID, MediaId::fromString) as MediaId.Category

    val itemOrdinal = mediaId.category.ordinal

    private val dataPublisher = MutableStateFlow<List<RecentlyAddedFragmentModel>>(emptyList())
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

    fun observeData(): Flow<List<RecentlyAddedFragmentModel>> = dataPublisher
    fun observeTitle(): Flow<String> = titlePublisher

    private fun Track.toRecentDetailDisplayableItem(): RecentlyAddedFragmentModel {
        return RecentlyAddedFragmentModel(
            mediaId = MediaId.playableItem(mediaId, id),
            title = title,
            subtitle = DisplayableItemUtils.trackSubtitle(artist, album),
        )
    }


}
