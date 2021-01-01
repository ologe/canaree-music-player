package dev.olog.domain.interactor.sort

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.domain.entity.Sort
import dev.olog.domain.interactor.base.FlowUseCaseWithParam
import dev.olog.domain.prefs.SortPreferencesGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDetailSortUseCase @Inject constructor(
    private val gateway: SortPreferencesGateway

) : FlowUseCaseWithParam<Sort, MediaId>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCase(mediaId: MediaId): Flow<Sort> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> gateway.observeDetailFolderSort()
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> gateway.observeDetailPlaylistSort()
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> gateway.observeDetailAlbumSort()
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> gateway.observeDetailArtistSort()
            MediaIdCategory.GENRES -> gateway.observeDetailGenreSort()
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }

}