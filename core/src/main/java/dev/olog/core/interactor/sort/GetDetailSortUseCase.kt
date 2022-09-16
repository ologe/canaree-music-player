package dev.olog.core.interactor.sort

import dev.olog.core.MediaId
import dev.olog.core.entity.sort.Sort
import javax.inject.Inject

class GetDetailSortUseCase @Inject constructor(

) {

    operator fun invoke(mediaId: MediaId): Sort {
        TODO("")
//        val category = mediaId.category
//        return when (category) {
//            MediaIdCategory.FOLDERS -> gateway.getDetailFolderSort()
//            MediaIdCategory.PLAYLISTS,
//            MediaIdCategory.PODCASTS_PLAYLIST -> gateway.getDetailPlaylistSort()
//            MediaIdCategory.ALBUMS,
//            MediaIdCategory.PODCASTS_ALBUMS -> gateway.getDetailAlbumSort()
//            MediaIdCategory.ARTISTS,
//            MediaIdCategory.PODCASTS_ARTISTS -> gateway.getDetailArtistSort()
//            MediaIdCategory.GENRES -> gateway.getDetailGenreSort()
//            else -> throw IllegalArgumentException("invalid media id $mediaId")
//        }
    }

}