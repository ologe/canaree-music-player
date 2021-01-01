package dev.olog.domain.interactor

import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.track.FolderGateway
import dev.olog.domain.gateway.track.GenreGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveMostPlayedSongsUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway
) {

    operator fun invoke(mediaId: MediaId): Flow<List<Track>> {
        return when (mediaId.category) {
            MediaIdCategory.GENRES -> genreGateway.observeMostPlayed(mediaId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeMostPlayed(mediaId)
            MediaIdCategory.FOLDERS -> folderGateway.observeMostPlayed(mediaId)
            else -> flowOf(listOf())
        }
    }
}