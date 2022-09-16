package dev.olog.core.interactor.sort

import dev.olog.core.MediaId
import dev.olog.core.entity.sort.Sort
import dev.olog.core.interactor.base.FlowUseCaseWithParam
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDetailSortUseCase @Inject constructor(

) : FlowUseCaseWithParam<Sort, MediaId>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCase(mediaId: MediaId): Flow<Sort> {
        TODO()
//        return when (mediaId.category) {
//            MediaIdCategory.FOLDERS -> gateway.observeDetailFolderSort()
//            MediaIdCategory.PLAYLISTS,
//            MediaIdCategory.PODCASTS_PLAYLIST -> gateway.observeDetailPlaylistSort()
//            MediaIdCategory.ALBUMS,
//            MediaIdCategory.PODCASTS_ALBUMS -> gateway.observeDetailAlbumSort()
//            MediaIdCategory.ARTISTS,
//            MediaIdCategory.PODCASTS_ARTISTS -> gateway.observeDetailArtistSort()
//            MediaIdCategory.GENRES -> gateway.observeDetailGenreSort()
//            else -> throw IllegalArgumentException("invalid media id $mediaId")
//        }
    }

}