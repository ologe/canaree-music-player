package dev.olog.presentation.tab

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.prefs.SortPreferences
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.PresentationPreferencesGateway
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
internal class TabFragmentViewModel @Inject constructor(
    private val dataProvider: TabDataProvider,
    private val appPreferencesUseCase: SortPreferences,
    private val presentationPrefs: PresentationPreferencesGateway

) : ViewModel() {

    private val liveDataMap: MutableMap<TabCategory, LiveData<List<DisplayableItem>>> =
        mutableMapOf()

    fun observeData(category: TabCategory, isPodcast: Boolean): LiveData<List<DisplayableItem>> {
        return liveDataMap.getOrPut(category) {
            dataProvider.get(category, isPodcast).asLiveData()
        }
    }

    fun getRecentlyAddedAlbums(isPodcast: Boolean): LiveData<List<DisplayableItem>> {
        return dataProvider.getRecentlyAddedAlbums(isPodcast).asLiveData()
    }
    fun getRecentlyAddedArtists(isPodcast: Boolean): LiveData<List<DisplayableItem>> {
        return dataProvider.getRecentlyAddedArtists(isPodcast).asLiveData()
    }
    fun getRecentlyPlayedAlbums(isPodcast: Boolean): LiveData<List<DisplayableItem>> {
        return dataProvider.getRecentlyPlayedAlbums(isPodcast).asLiveData()
    }
    fun getRecentlyPlayedArtists(isPodcast: Boolean): LiveData<List<DisplayableItem>> {
        return dataProvider.getRecentlyPlayedArtists(isPodcast).asLiveData()
    }

    fun getAllTracksSortOrder(isPodcast: Boolean): SortEntity? {
        if (isPodcast) {
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

    fun getSpanCount(category: TabCategory) = presentationPrefs.getSpanCount(category)
    fun observeSpanCount(category: TabCategory) = presentationPrefs.observeSpanCount(category)

}