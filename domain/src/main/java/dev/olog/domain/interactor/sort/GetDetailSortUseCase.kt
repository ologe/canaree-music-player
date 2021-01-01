package dev.olog.domain.interactor.sort

import dev.olog.domain.entity.Sort
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.domain.prefs.SortPreferencesGateway
import javax.inject.Inject

class GetDetailSortUseCase @Inject constructor(
    private val gateway: SortPreferencesGateway
) {

    operator fun invoke(mediaId: MediaId): Sort {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> gateway.getDetailFolderSort()
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> gateway.getDetailPlaylistSort()
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> gateway.getDetailAlbumSort()
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> gateway.getDetailArtistSort()
            MediaIdCategory.GENRES -> gateway.getDetailGenreSort()

            MediaIdCategory.SONGS,
            MediaIdCategory.PODCASTS -> error("invalid mediaid=$mediaId")
        }
    }

}