package dev.olog.presentation.tab

import androidx.lifecycle.ViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.prefs.SortPreferences
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.PresentationPreferencesGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class TabFragmentViewModel @Inject constructor(
    private val schedulers: Schedulers,
    private val dataProvider: TabDataProvider,
    private val appPreferencesUseCase: SortPreferences,
    private val presentationPrefs: PresentationPreferencesGateway

) : ViewModel() {

    fun observeData(category: TabCategory): Flow<List<DisplayableItem>> {
        return dataProvider.get(category)
            .flowOn(schedulers.io)
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