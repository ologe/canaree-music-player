package dev.olog.presentation.tab

import androidx.lifecycle.ViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.prefs.SortPreferences
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.PresentationPreferencesGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class TabFragmentViewModel @Inject constructor(
    private val dataProvider: TabDataProvider,
    private val appPreferencesUseCase: SortPreferences,
    private val presentationPrefs: PresentationPreferencesGateway

) : ViewModel() {

    private val liveDataMap: MutableMap<TabCategory, Flow<List<DisplayableItem>>> =
        mutableMapOf()

    fun observeData(category: TabCategory): Flow<List<DisplayableItem>> {
        return liveDataMap.getOrPut(category) {
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

//    fun getAllArtistsSortOrder(): SortEntity {
//        return appPreferencesUseCase.getAllArtistsSort()
//    }

    fun getSpanCount(category: TabCategory) = presentationPrefs.getSpanCount(category)
    fun observeSpanCount(category: TabCategory) = presentationPrefs.observeSpanCount(category)

}