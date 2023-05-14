package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.AutoPlaylistGateway
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.base.FlowUseCaseWithParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetItemTitleUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val autoPlaylistGateway: AutoPlaylistGateway,
) : FlowUseCaseWithParam<String, MediaId>() {


    override fun buildUseCase(param: MediaId): Flow<String> {
        return when (param.category){
            MediaIdCategory.FOLDERS -> folderGateway.observeById(param.id).map { it?.title }
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeById(param.id).map { it?.title }
            MediaIdCategory.SONGS -> songGateway.observeById(param.id).map { it?.title }
            MediaIdCategory.ALBUMS -> albumGateway.observeById(param.id).map { it?.title }
            MediaIdCategory.ARTISTS -> artistGateway.observeById(param.id).map { it?.name }
            MediaIdCategory.GENRES -> genreGateway.observeById(param.id).map { it?.name }
            MediaIdCategory.AUTO_PLAYLISTS -> autoPlaylistGateway.observeById(param.id).map { it?.title }
            MediaIdCategory.HEADER,
            MediaIdCategory.PLAYING_QUEUE -> error("invalid media category ${param.category}")
        }.map { it ?: "" }
    }

}