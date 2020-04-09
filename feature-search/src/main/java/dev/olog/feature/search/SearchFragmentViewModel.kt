package dev.olog.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.domain.interactor.search.ClearRecentSearchesUseCase
import dev.olog.domain.interactor.search.DeleteRecentSearchUseCase
import dev.olog.domain.interactor.search.InsertRecentSearchUseCase
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.feature.presentation.base.prefs.CommonPreferences
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class SearchFragmentViewModel @Inject constructor(
    private val dataProvider: SearchDataProvider,
    private val insertRecentUse: InsertRecentSearchUseCase,
    private val deleteRecentSearchUseCase: DeleteRecentSearchUseCase,
    private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase,
    private val preferences: CommonPreferences,
    private val schedulers: Schedulers

) : ViewModel() {

    private val isPodcastPublisher = ConflatedBroadcastChannel(false)

    fun canShowPodcasts() = preferences.canShowPodcasts()

    val data: Flow<List<DisplayableItem>> = isPodcastPublisher.asFlow()
        .flatMapLatest { dataProvider.observe(it) }
        .flowOn(schedulers.cpu)

    val artistsData: Flow<List<DisplayableAlbum>> = isPodcastPublisher.asFlow()
        .flatMapLatest { dataProvider.observeArtists(it) }
        .flowOn(schedulers.cpu)

    val albumsData: Flow<List<DisplayableAlbum>> = isPodcastPublisher.asFlow()
        .flatMapLatest { dataProvider.observeAlbums(it) }
        .flowOn(schedulers.cpu)

    val genresData: Flow<List<DisplayableAlbum>> = isPodcastPublisher.asFlow()
        .flatMapLatest { dataProvider.observeGenres(it) }
        .flowOn(schedulers.cpu)

    val playlistsData: Flow<List<DisplayableAlbum>> = isPodcastPublisher.asFlow()
        .flatMapLatest { dataProvider.observePlaylists(it) }
        .flowOn(schedulers.cpu)

    val foldersData: Flow<List<DisplayableAlbum>> = isPodcastPublisher.asFlow()
        .flatMapLatest { dataProvider.observeFolders(it) }
        .flowOn(schedulers.cpu)

    override fun onCleared() {
        super.onCleared()
        isPodcastPublisher.close()
        dataProvider.dispose()
    }

    fun updateShowPodcast(showPodcast: Boolean) {
        isPodcastPublisher.offer(showPodcast)
    }

    fun updateQuery(newQuery: String) {
        dataProvider.updateQuery(newQuery.trim())
    }

    fun insertToRecent(mediaId: PresentationId) = viewModelScope.launch(schedulers.io) {
        insertRecentUse(mediaId.toDomain())
    }

    fun deleteFromRecent(mediaId: PresentationId) = viewModelScope.launch(schedulers.io) {
        deleteRecentSearchUseCase(mediaId.toDomain())
    }

    fun clearRecentSearches() = viewModelScope.launch(schedulers.io) {
        clearRecentSearchesUseCase()
    }

}