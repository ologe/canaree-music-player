package dev.olog.core.interactor.mostplayed

import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.*
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.PlaylistGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveMostPlayedSongsUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway

) {

    operator fun invoke(mediaId: Category): Flow<List<Song>> {
        return when (mediaId.category) {
            GENRES -> genreGateway.observeMostPlayed(mediaId)
            PLAYLISTS -> playlistGateway.observeMostPlayed(mediaId)
            FOLDERS -> folderGateway.observeMostPlayed(mediaId)
            else -> flowOf(emptyList())
        }
    }
}