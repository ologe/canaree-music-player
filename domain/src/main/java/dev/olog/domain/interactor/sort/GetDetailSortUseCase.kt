package dev.olog.domain.interactor.sort

import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.entity.sort.SortArranging
import dev.olog.domain.entity.sort.SortEntity
import dev.olog.domain.entity.sort.SortType
import dev.olog.domain.prefs.SortPreferences
import dev.olog.shared.throwNotHandled
import javax.inject.Inject

class GetDetailSortUseCase @Inject constructor(
    private val gateway: SortPreferences

) {

    operator fun invoke(mediaId: MediaId): SortEntity {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> gateway.getDetailFolderSort()
            MediaIdCategory.PLAYLISTS -> gateway.getDetailPlaylistSort()
            MediaIdCategory.ALBUMS -> gateway.getDetailAlbumSort()
            MediaIdCategory.ARTISTS -> gateway.getDetailArtistSort()
            MediaIdCategory.GENRES -> gateway.getDetailGenreSort()

            // here just to avoid crash or an ugly refactor
            MediaIdCategory.PODCASTS_AUTHORS,
            MediaIdCategory.PODCASTS_PLAYLIST,
            MediaIdCategory.GENERATED_PLAYLIST -> SortEntity(SortType.TITLE, SortArranging.ASCENDING)
            else -> throwNotHandled(mediaId)
        }
    }

}