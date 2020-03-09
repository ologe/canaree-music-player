package dev.olog.core.interactor.sort

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortPreferences
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
            MediaIdCategory.PODCASTS_AUTHOR,
            MediaIdCategory.PODCASTS_PLAYLIST -> flowOf(SortEntity(SortType.TITLE, SortArranging.ASCENDING))
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }

}