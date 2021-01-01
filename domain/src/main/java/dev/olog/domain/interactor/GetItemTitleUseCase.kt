package dev.olog.domain.interactor

import dev.olog.domain.gateway.podcast.PodcastAlbumGateway
import dev.olog.domain.gateway.podcast.PodcastArtistGateway
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.*
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetItemTitleUseCase @Inject constructor(
    private val getFolderUseCase: FolderGateway,
    private val getPlaylistUseCase: PlaylistGateway,
    private val getSongUseCase: SongGateway,
    private val getAlbumUseCase: AlbumGateway,
    private val getArtistUseCase: ArtistGateway,
    private val getGenreUseCase: GenreGateway,

    private val getPodcastPlaylistUseCase: PodcastPlaylistGateway,
    private val getPodcastUseCase: PodcastGateway,
    private val getPodcastAlbumUseCase: PodcastAlbumGateway,
    private val getPodcastArtistUseCase: PodcastArtistGateway
) {

    operator fun invoke(param: MediaId): Flow<String> {
        return when (param.category){
            MediaIdCategory.FOLDERS -> getFolderUseCase.observeByParam(param.categoryValue).map { it?.title }
            MediaIdCategory.PLAYLISTS -> getPlaylistUseCase.observeByParam(param.categoryId).map { it?.title }
            MediaIdCategory.SONGS -> getSongUseCase.observeByParam(param.categoryId).map { it?.title }
            MediaIdCategory.ALBUMS -> getAlbumUseCase.observeByParam(param.categoryId).map { it?.title }
            MediaIdCategory.ARTISTS -> getArtistUseCase.observeByParam(param.categoryId).map { it?.name }
            MediaIdCategory.GENRES -> getGenreUseCase.observeByParam(param.categoryId).map { it?.name }
            MediaIdCategory.PODCASTS_PLAYLIST -> getPodcastPlaylistUseCase.observeByParam(param.categoryId).map { it?.title }
            MediaIdCategory.PODCASTS -> getPodcastUseCase.observeByParam(param.categoryId).map { it?.title }
            MediaIdCategory.PODCASTS_ARTISTS -> getPodcastArtistUseCase.observeByParam(param.categoryId).map { it?.name }
            MediaIdCategory.PODCASTS_ALBUMS -> getPodcastAlbumUseCase.observeByParam(param.categoryId).map { it?.title }
        }.map { it ?: "" }
    }

}