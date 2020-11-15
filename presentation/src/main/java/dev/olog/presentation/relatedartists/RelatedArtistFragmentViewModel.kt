package dev.olog.presentation.relatedartists

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
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.relatedartists.RelatedArtistFragment.Companion.ARGUMENTS_MEDIA_ID
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

    private val mediaId = state.argument(ARGUMENTS_MEDIA_ID, MediaId::fromString)

    val itemOrdinal = mediaId.category.ordinal

    private val dataPublisher = MutableStateFlow<List<DisplayableItem>>(emptyList())
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

    fun observeData(): Flow<List<DisplayableItem>> = dataPublisher
    fun observeTitle(): Flow<String> = titlePublisher

    private fun Artist.toRelatedArtist(): DisplayableItem {
        val songs = context.resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)

        return DisplayableAlbum(
            type = R.layout.item_related_artist,
            mediaId = getMediaId(),
            title = this.name,
            subtitle = songs
        )
    }

}