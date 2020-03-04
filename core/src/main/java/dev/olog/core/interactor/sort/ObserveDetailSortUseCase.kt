package dev.olog.core.interactor.sort

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.prefs.SortPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDetailSortUseCase @Inject constructor(
    private val gateway: SortPreferences

) {

    operator fun invoke(mediaId: MediaId): Flow<SortEntity> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> gateway.observeDetailFolderSort()
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> gateway.observeDetailPlaylistSort()
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> gateway.observeDetailAlbumSort()
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> gateway.observeDetailArtistSort()
            MediaIdCategory.GENRES -> gateway.observeDetailGenreSort()
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }

}