package dev.olog.feature.detail.related.artist

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.interactor.GetItemTitleUseCase
import dev.olog.core.interactor.ObserveRelatedArtistsUseCase
import dev.olog.feature.detail.R
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class RelatedArtistFragmentViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    @ApplicationContext private val context: Context,
    useCase: ObserveRelatedArtistsUseCase,
    getItemTitleUseCase: GetItemTitleUseCase

) : ViewModel() {

    private val mediaId = state.argument(Params.MEDIA_ID, MediaId::fromString)

    val itemOrdinal = mediaId.category.ordinal

    private val dataPublisher = MutableStateFlow<List<RelatedArtistFragmentModel>>(emptyList())
    private val titlePublisher = MutableStateFlow("")

    init {
        useCase(mediaId)
            .mapListItem { it.toRelatedArtist() }
            .flowOn(Dispatchers.IO)
            .onEach { dataPublisher.value = it }
            .launchIn(viewModelScope)

        getItemTitleUseCase(mediaId)
            .flowOn(Dispatchers.IO)
            .onEach { titlePublisher.value = it }
            .launchIn(viewModelScope)
    }

    fun observeData(): Flow<List<RelatedArtistFragmentModel>> = dataPublisher
    fun observeTitle(): Flow<String> = titlePublisher

    private fun Artist.toRelatedArtist(): RelatedArtistFragmentModel {
        val songs = context.resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)

        return RelatedArtistFragmentModel(
            mediaId = getMediaId(),
            title = this.name,
            subtitle = songs
        )
    }

}