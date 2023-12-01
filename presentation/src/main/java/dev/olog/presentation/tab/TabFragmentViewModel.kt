package dev.olog.presentation.tab

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortPreferences
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.tab.adapter.TabFragmentItem
import dev.olog.presentation.widgets.fascroller.ScrollableItem
import dev.olog.shared.android.extensions.asLiveData
import dev.olog.shared.android.extensions.map
import javax.inject.Inject

@HiltViewModel
internal class TabFragmentViewModel @Inject constructor(
    private val dataProvider: TabDataProvider,
    private val appPreferencesUseCase: SortPreferences,
    private val presentationPrefs: PresentationPreferencesGateway
) : ViewModel() {

    fun observeData(category: TabCategory): LiveData<List<TabFragmentItem>> {
        return observeSpanCount(category).asLiveData().switchMap { spanCount ->
            dataProvider.get(category, spanCount).asLiveData()
        }
    }

    fun observeSortLetters(category: TabCategory): LiveData<List<String>> {
        return observeData(category).map { list ->
            val sort = getSort(category) // TODO combine with flow instead
            list.asSequence()
                .filterIsInstance<ScrollableItem>()
                .mapNotNull { it.getText(sort.type).firstOrNull()?.uppercase() }
                .distinct()
                .map { it }
                .toList()
        }
    }

    fun getAllTracksSortOrder(mediaId: MediaId): SortEntity? {
        if (mediaId.isAnyPodcast) {
            return null
        }
        return appPreferencesUseCase.getAllTracksSort()
    }

    fun getAllAlbumsSortOrder(): SortEntity {
        return appPreferencesUseCase.getAllAlbumsSort()
    }

    fun getAllArtistsSortOrder(): SortEntity {
        return appPreferencesUseCase.getAllArtistsSort()
    }

    fun getSort(category: TabCategory): SortEntity = when (category) {
        TabCategory.SONGS -> appPreferencesUseCase.getAllTracksSort()
        TabCategory.ALBUMS -> appPreferencesUseCase.getAllAlbumsSort()
        TabCategory.ARTISTS -> appPreferencesUseCase.getAllArtistsSort()
        TabCategory.FOLDERS,
        TabCategory.PLAYLISTS,
        TabCategory.GENRES,
        TabCategory.PODCASTS_PLAYLIST,
        TabCategory.PODCASTS,
        TabCategory.PODCASTS_ARTISTS,
        TabCategory.PODCASTS_ALBUMS -> SortEntity(SortType.TITLE, SortArranging.ASCENDING)
    }

    fun getSpanCount(category: TabCategory) = presentationPrefs.getSpanCount(category)
    fun observeSpanCount(category: TabCategory) = presentationPrefs.observeSpanCount(category)

}