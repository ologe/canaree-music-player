package dev.olog.feature.library.tab

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.AllAlbumsSort
import dev.olog.core.entity.sort.AllArtistsSort
import dev.olog.core.entity.sort.AllSongsSort
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.feature.library.api.LibraryPreferences
import dev.olog.feature.library.api.TabCategory
import dev.olog.ui.model.DisplayableItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
internal class TabFragmentViewModel @Inject constructor(
    private val dataProvider: TabDataProvider,
    private val libraryPrefs: LibraryPreferences,
    private val songGateway: SongGateway,
    private val artistGateway: ArtistGateway,
    private val albumGateway: AlbumGateway,
) : ViewModel() {

    private val dataMap = mutableMapOf<TabCategory, Flow<List<DisplayableItem>>>()

    fun observeData(category: TabCategory): Flow<List<DisplayableItem>> {
        return dataMap.getOrPut(category) {
            dataProvider.get(category)
        }
    }

    fun getAllTracksSortOrder(mediaId: MediaId): AllSongsSort? {
        // todo podcast sort
        if (mediaId.isAnyPodcast) {
            return null
        }
        return songGateway.getSort()
    }

    fun getAllAlbumsSortOrder(): AllAlbumsSort {
        // todo podcast sort
        return albumGateway.getSort()
    }

    fun getAllArtistsSortOrder(): AllArtistsSort {
        // todo podcast sort
        return artistGateway.getSort()
    }

    fun getSpanCount(category: TabCategory) = libraryPrefs.getSpanCount(category)
    fun observeSpanCount(category: TabCategory) = libraryPrefs.observeSpanCount(category)

}