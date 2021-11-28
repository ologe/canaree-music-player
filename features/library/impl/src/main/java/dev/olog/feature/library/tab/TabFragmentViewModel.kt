package dev.olog.feature.library.tab

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.entity.sort.Sort
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.feature.base.model.DisplayableItem
import dev.olog.feature.library.LibraryPrefs
import dev.olog.feature.library.TabCategory
import dev.olog.shared.android.extensions.asLiveData
import javax.inject.Inject

@HiltViewModel
class TabFragmentViewModel @Inject constructor(
    private val dataProvider: TabDataProvider,
    private val songGateway: SongGateway,
    private val artistGateway: ArtistGateway,
    private val albumGateway: AlbumGateway,
    private val libraryPrefs: LibraryPrefs,

    ) : ViewModel() {

    private val liveDataMap: MutableMap<TabCategory, LiveData<List<DisplayableItem>>> =
        mutableMapOf()

    fun observeData(category: TabCategory): LiveData<List<DisplayableItem>> {
        var liveData = liveDataMap[category]
        if (liveData == null) {
            liveData = dataProvider.get(category).asLiveData()
        }
        return liveData
    }

    fun getAllTracksSortOrder(isPodcast: Boolean): Sort? {
        if (isPodcast) {
            return null
        }
        return songGateway.sort
    }

    fun getAllAlbumsSortOrder(): Sort {
        return albumGateway.sort
    }

    fun getAllArtistsSortOrder(): Sort {
        return artistGateway.sort
    }

    fun getSpanCount(category: TabCategory) = libraryPrefs.spanCount(category).get()
    fun observeSpanCount(category: TabCategory) = libraryPrefs.spanCount(category).observe()

}