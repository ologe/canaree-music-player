package dev.olog.domain.interactor.sort

import dev.olog.domain.entity.Sort
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.domain.prefs.SortPreferencesGateway
import javax.inject.Inject

class SetSortOrderUseCase @Inject constructor(
    private val gateway: SortPreferencesGateway
) {

    operator fun invoke(
        mediaId: MediaId.Category,
        sortType: Sort.Type
    ) {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> gateway.setDetailFolderSort(sortType)
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> gateway.setDetailPlaylistSort(sortType)
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> gateway.setDetailAlbumSort(sortType)
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> gateway.setDetailArtistSort(sortType)
            MediaIdCategory.GENRES -> gateway.setDetailGenreSort(sortType)

            MediaIdCategory.SONGS,
            MediaIdCategory.PODCASTS -> error("invalid mediaid=$mediaId")
        }
    }
}