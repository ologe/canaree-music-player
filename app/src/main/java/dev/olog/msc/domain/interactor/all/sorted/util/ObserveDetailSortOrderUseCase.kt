package dev.olog.msc.domain.interactor.all.sorted.util

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortPreferences
import dev.olog.msc.domain.interactor.base.FlowUseCaseWithParam
import dev.olog.shared.extensions.asFlowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

class ObserveDetailSortOrderUseCase @Inject constructor(
    private val gateway: SortPreferences

) : FlowUseCaseWithParam<SortType, MediaId>() {

    override fun buildUseCase(mediaId: MediaId): Flow<SortType> {
        val category = mediaId.category
        return when (category) {
            MediaIdCategory.FOLDERS -> gateway.observeDetailFolderSortOrder().asFlowable().asFlow()
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> gateway.observeDetailPlaylistSortOrder().asFlowable().asFlow()
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> gateway.observeDetailAlbumSortOrder().asFlowable().asFlow()
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> gateway.observeDetailArtistSortOrder().asFlowable().asFlow()
            MediaIdCategory.GENRES -> gateway.observeDetailGenreSortOrder().asFlowable().asFlow()
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }

}