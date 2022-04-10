package dev.olog.presentation.tab

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.prefs.SortPreferences
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.android.extensions.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal class TabFragmentViewModel @Inject constructor(
    private val dataProvider: TabDataProvider,
    private val appPreferencesUseCase: SortPreferences,
    private val presentationPrefs: PresentationPreferencesGateway

) : ViewModel() {

    private val liveDataMap: MutableMap<TabCategory, LiveData<List<DisplayableItem>>> =
        mutableMapOf()

    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION") // kotlin compiler error
    suspend fun observeData(category: TabCategory): LiveData<List<DisplayableItem>> {
        return withContext(Dispatchers.Default) {
            var liveData = liveDataMap[category]
            if (liveData == null) {
                liveData = dataProvider.get(category).asLiveData()
            }
            liveData!!
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

    fun getSpanCount(category: TabCategory) = presentationPrefs.getSpanCount(category)
    fun observeSpanCount(category: TabCategory) = presentationPrefs.observeSpanCount(category)

}