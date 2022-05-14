package dev.olog.feature.library.tab

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.prefs.SortPreferences
import dev.olog.feature.library.api.TabCategory
import dev.olog.feature.library.api.LibraryPreferences
import dev.olog.ui.model.DisplayableItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
internal class TabFragmentViewModel @Inject constructor(
    private val dataProvider: TabDataProvider,
    private val appPreferencesUseCase: SortPreferences,
    private val libraryPrefs: LibraryPreferences,

) : ViewModel() {

    private val dataMap = mutableMapOf<TabCategory, Flow<List<DisplayableItem>>>()

    fun observeData(category: TabCategory): Flow<List<DisplayableItem>> {
        return dataMap.getOrPut(category) {
            dataProvider.get(category)
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

    fun getSpanCount(category: TabCategory) = libraryPrefs.getSpanCount(category)
    fun observeSpanCount(category: TabCategory) = libraryPrefs.observeSpanCount(category)

}