package dev.olog.domain.interactor.sort

import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.entity.sort.SortType
import dev.olog.domain.prefs.SortPreferences
import javax.inject.Inject

class SetSortOrderUseCase @Inject constructor(
    private val gateway: SortPreferences
) {

    class Request(
        val mediaId: MediaId,
        val sortType: SortType
    )

    operator fun invoke(param: Request) {
        return when (param.mediaId.category) {
            MediaIdCategory.FOLDERS -> gateway.setDetailFolderSort(param.sortType)
            MediaIdCategory.PLAYLISTS -> gateway.setDetailPlaylistSort(param.sortType)
            MediaIdCategory.ALBUMS -> gateway.setDetailAlbumSort(param.sortType)
            MediaIdCategory.ARTISTS -> gateway.setDetailArtistSort(param.sortType)
            MediaIdCategory.GENRES -> gateway.setDetailGenreSort(param.sortType)

            // here just to avoid crash or an ugly refactor
            MediaIdCategory.PODCASTS_PLAYLIST,
            MediaIdCategory.PODCASTS_AUTHORS,
            MediaIdCategory.GENERATED_PLAYLIST -> {}
            else -> throw IllegalArgumentException("invalid param $param")
        }
    }
}