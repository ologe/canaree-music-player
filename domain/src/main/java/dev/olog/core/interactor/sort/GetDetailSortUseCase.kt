package dev.olog.core.interactor.sort

import dev.olog.core.mediaid.MediaId
import dev.olog.core.mediaid.MediaIdCategory
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.prefs.SortPreferencesGateway
import javax.inject.Inject

class GetDetailSortUseCase @Inject constructor(
    private val gateway: SortPreferencesGateway

) {

    operator fun invoke(mediaId: MediaId): SortEntity {
        val category = mediaId.category
        return when (category) {
            MediaIdCategory.FOLDERS -> gateway.getDetailFolderSort()
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> gateway.getDetailPlaylistSort()
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> gateway.getDetailAlbumSort()
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> gateway.getDetailArtistSort()
            MediaIdCategory.GENRES -> gateway.getDetailGenreSort()
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }

}