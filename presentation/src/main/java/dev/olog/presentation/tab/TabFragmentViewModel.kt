package dev.olog.presentation.tab

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.prefs.SortPreferences
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class TabFragmentViewModel @Inject constructor(
        private val tabDataProvider: TabDataProvider,
        private val appPreferencesUseCase: SortPreferences

) : ViewModel() {

    private val liveDataMap: MutableMap<TabCategory, LiveData<List<DisplayableItem>>> = mutableMapOf()

    suspend fun observeData(category: TabCategory): LiveData<List<DisplayableItem>> {
        return withContext(Dispatchers.Default) {
            var liveData = liveDataMap[category]
            if (liveData == null) {
                liveData = tabDataProvider.get(category).asLiveData()
            }
            liveData!!
        }
    }

    fun getAllTracksSortOrder(): SortEntity {
        return appPreferencesUseCase.getAllTracksSortOrder()
    }

    fun getAllAlbumsSortOrder(): SortEntity {
        return appPreferencesUseCase.getAllAlbumsSortOrder()
    }

    fun getAllArtistsSortOrder(): SortEntity {
        return appPreferencesUseCase.getAllArtistsSortOrder()
    }

//    fun observeAlbumSpanSize(category: MediaIdCategory): Observable<GridSpanSize> {
//        return appPreferencesUseCase.observeSpanSize(category)
//    }

}