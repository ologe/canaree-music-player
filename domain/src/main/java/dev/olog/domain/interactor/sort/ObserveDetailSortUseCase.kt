package dev.olog.domain.interactor.sort

import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.entity.sort.SortArranging
import dev.olog.domain.entity.sort.SortEntity
import dev.olog.domain.entity.sort.SortType
import dev.olog.domain.prefs.SortPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveDetailSortUseCase @Inject constructor(
    private val gateway: SortPreferences

) {

    operator fun invoke(mediaId: MediaId): Flow<SortEntity> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> gateway.observeDetailFolderSort()
            MediaIdCategory.PLAYLISTS -> gateway.observeDetailPlaylistSort()
            MediaIdCategory.ALBUMS -> gateway.observeDetailAlbumSort()
            MediaIdCategory.ARTISTS -> gateway.observeDetailArtistSort()
            MediaIdCategory.GENRES -> gateway.observeDetailGenreSort()

            // here just to avoid crash or an ugly refactor
            MediaIdCategory.PODCASTS_AUTHORS,
            MediaIdCategory.PODCASTS_PLAYLIST -> flowOf(SortEntity(SortType.TITLE, SortArranging.ASCENDING))
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }

}