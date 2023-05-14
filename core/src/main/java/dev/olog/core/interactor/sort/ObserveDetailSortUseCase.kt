package dev.olog.core.interactor.sort

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.interactor.base.FlowUseCaseWithParam
import dev.olog.core.prefs.SortPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDetailSortUseCase @Inject constructor(
    private val gateway: SortPreferences

) : FlowUseCaseWithParam<SortEntity, MediaId>() {

    // TODO separate sort (songs and podcasts)
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCase(mediaId: MediaId): Flow<SortEntity> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> gateway.observeDetailFolderSort()
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.AUTO_PLAYLISTS -> gateway.observeDetailPlaylistSort()
            MediaIdCategory.ALBUMS -> gateway.observeDetailAlbumSort()
            MediaIdCategory.ARTISTS -> gateway.observeDetailArtistSort()
            MediaIdCategory.GENRES -> gateway.observeDetailGenreSort()
            MediaIdCategory.SONGS,
            MediaIdCategory.HEADER,
            MediaIdCategory.PLAYING_QUEUE -> error("invalid media id $mediaId")
        }
    }

}