package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory.*
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.shared.throwNotHandled
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetItemTitleUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,

    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastAuthorGateway: PodcastAuthorGateway

) {


    operator fun invoke(mediaId: MediaId): Flow<String> {
        return when (mediaId.category){
            FOLDERS -> folderGateway.observeByParam(mediaId.categoryId).map { it!!.title }
            PLAYLISTS -> playlistGateway.observeByParam(mediaId.categoryId.toLong()).map { it!!.title }
            ALBUMS -> albumGateway.observeByParam(mediaId.categoryId.toLong()).map { it!!.title }
            ARTISTS -> artistGateway.observeByParam(mediaId.categoryId.toLong()).map { it!!.name }
            GENRES -> genreGateway.observeByParam(mediaId.categoryId.toLong()).map { it!!.name }
            PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeByParam(mediaId.categoryId.toLong()).map { it!!.title }
            PODCASTS_AUTHORS -> podcastAuthorGateway.observeByParam(mediaId.categoryId.toLong()).map { it!!.name }
            SONGS -> throwNotHandled(mediaId)
            PODCASTS -> throwNotHandled(mediaId)
            SPOTIFY_ALBUMS -> throwNotHandled(mediaId)
        }
    }

}