package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Playlist
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.songlist.ObserveSongListByParamUseCase
import javax.inject.Inject

class AddToPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val getSongUseCase: SongGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val getPodcastUseCase: PodcastGateway,
    private val getSongListByParamUseCase: ObserveSongListByParamUseCase

) {

    operator fun invoke(playlist: Playlist, mediaId: MediaId) {
        TODO()

//        if (mediaId.isLeaf && mediaId.isPodcast){
//            return getPodcastUseCase.execute(mediaId)
//                    .firstOrError()
//                    .flatMapCompletable { podcastPlaylistGateway.addSongsToPlaylist(playlist.id, listOf(mediaId.resolveId)) }
//        }
//
//        if (mediaId.isLeaf) {
//            return getSongUseCase.execute(mediaId)
//                    .firstOrError()
//                    .flatMapCompletable { playlistGateway.addSongsToPlaylist(playlist.id, listOf(mediaId.resolveId)) }
//        }
//
//        return getSongListByParamUseCase(mediaId)
//                .asFlowable()
//                .firstOrError()
//                .mapToList { it.id }
//                .flatMapCompletable {
//                    if (mediaId.isAnyPodcast){
//                        podcastPlaylistGateway.addSongsToPlaylist(playlist.id, it)
//                    } else {
//                        playlistGateway.addSongsToPlaylist(playlist.id, it)
//                    }
//
//                }
    }
}