package dev.olog.core.interactor.sort

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortPreferences
import javax.inject.Inject

class SetSortOrderUseCase @Inject constructor(
    private val gateway: SortPreferences
) {

    class Request(
        val mediaId: MediaId,
        val sortType: SortType
    )

    // TODO separate sort (songs and podcasts)
    operator fun invoke(param: Request) {
        val category = param.mediaId.category
        return when (category) {
            MediaIdCategory.FOLDERS -> gateway.setDetailFolderSort(param.sortType)
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.AUTO_PLAYLISTS -> gateway.setDetailPlaylistSort(param.sortType)
            MediaIdCategory.ALBUMS -> gateway.setDetailAlbumSort(param.sortType)
            MediaIdCategory.ARTISTS -> gateway.setDetailArtistSort(param.sortType)
            MediaIdCategory.GENRES -> gateway.setDetailGenreSort(param.sortType)
            MediaIdCategory.SONGS,
            MediaIdCategory.HEADER,
            MediaIdCategory.PLAYING_QUEUE -> error("invalid param $param")
        }
    }
}