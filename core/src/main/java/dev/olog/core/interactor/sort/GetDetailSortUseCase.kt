package dev.olog.core.interactor.sort

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortPreferences
import javax.inject.Inject

class GetDetailSortUseCase @Inject constructor(
    private val gateway: SortPreferences

) {

    operator fun invoke(mediaId: MediaId): SortEntity {
        val category = mediaId.category
        return when (category) {
            MediaIdCategory.FOLDERS -> gateway.getDetailFolderSort()
            MediaIdCategory.PLAYLISTS -> gateway.getDetailPlaylistSort()
            MediaIdCategory.ALBUMS -> gateway.getDetailAlbumSort()
            MediaIdCategory.ARTISTS -> gateway.getDetailArtistSort()
            MediaIdCategory.GENRES -> gateway.getDetailGenreSort()

            // here just to avoid crash or an ugly refactor
            MediaIdCategory.PODCASTS_AUTHORS,
            MediaIdCategory.PODCASTS_PLAYLIST -> SortEntity(SortType.TITLE, SortArranging.ASCENDING)
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }

}