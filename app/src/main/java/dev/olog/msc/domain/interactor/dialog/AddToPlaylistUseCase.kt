package dev.olog.msc.domain.interactor.dialog

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Playlist
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.interactor.base.CompletableUseCaseWithParam
import dev.olog.core.interactor.songlist.ObserveSongListByParamUseCase
import dev.olog.msc.domain.interactor.item.GetPodcastUseCase
import dev.olog.msc.domain.interactor.item.GetSongUseCase
import dev.olog.shared.extensions.mapToList
import io.reactivex.Completable
import kotlinx.coroutines.rx2.asFlowable
import javax.inject.Inject

class AddToPlaylistUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val playlistGateway: PlaylistGateway,
    private val getSongUseCase: GetSongUseCase,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val getPodcastUseCase: GetPodcastUseCase,
    private val getSongListByParamUseCase: ObserveSongListByParamUseCase

) : CompletableUseCaseWithParam<Pair<Playlist, MediaId>>(scheduler) {

    override fun buildUseCaseObservable(param: Pair<Playlist, MediaId>): Completable {
        val (playlist, mediaId) = param

        if (mediaId.isLeaf && mediaId.isPodcast){
            return getPodcastUseCase.execute(mediaId)
                    .firstOrError()
                    .flatMapCompletable { podcastPlaylistGateway.addSongsToPlaylist(playlist.id, listOf(mediaId.resolveId)) }
        }

        if (mediaId.isLeaf) {
            return getSongUseCase.execute(mediaId)
                    .firstOrError()
                    .flatMapCompletable { playlistGateway.addSongsToPlaylist(playlist.id, listOf(mediaId.resolveId)) }
        }

        return getSongListByParamUseCase(mediaId)
                .asFlowable()
                .firstOrError()
                .mapToList { it.id }
                .flatMapCompletable {
                    if (mediaId.isAnyPodcast){
                        podcastPlaylistGateway.addSongsToPlaylist(playlist.id, it)
                    } else {
                        playlistGateway.addSongsToPlaylist(playlist.id, it)
                    }

                }
    }
}