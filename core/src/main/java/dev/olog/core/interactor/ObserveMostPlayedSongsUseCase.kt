package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FolderGateway
import dev.olog.core.gateway.GenreGateway
import dev.olog.core.gateway.PlaylistGateway
import dev.olog.core.interactor.base.FlowUseCaseWithParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ObserveMostPlayedSongsUseCase @Inject constructor(
        private val folderGateway: FolderGateway,
        private val playlistGateway: PlaylistGateway,
        private val genreGateway: GenreGateway

) : FlowUseCaseWithParam<List<Song>, MediaId>() {

    override fun buildUseCase(mediaId: MediaId): Flow<List<Song>> {
        return when (mediaId.category) {
            MediaIdCategory.GENRES -> return genreGateway.observeMostPlayed(mediaId)
            MediaIdCategory.PLAYLISTS -> return playlistGateway.observeMostPlayed(mediaId)
            MediaIdCategory.FOLDERS -> folderGateway.observeMostPlayed(mediaId)
            else -> flow {  }
        }
    }
}