package dev.olog.domain.interactor.dialog

import dev.olog.domain.entity.Playlist
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.base.SingleUseCaseWithParam
import dev.olog.domain.interactor.detail.item.GetSongUseCase
import dev.olog.shared.MediaId
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class AddToPlaylistUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val playlistGateway: PlaylistGateway,
        private val getSongUseCase: GetSongUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : SingleUseCaseWithParam<Pair<String, String>, Pair<Playlist, MediaId>>(scheduler) {

    override fun buildUseCaseObservable(param: Pair<Playlist, MediaId>): Single<Pair<String, String>> {
        val (playlist, mediaId) = param

        if (mediaId.isLeaf) {
            val songId = mediaId.leaf!!
            return getSongUseCase.execute(mediaId)
                    .firstOrError()
                    .flatMap { song -> playlistGateway.addSongsToPlaylist(playlist.id, listOf(songId))
                            .map { song }
                    }
                    .map { Pair(it.title, playlist.title) }
        }

        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .flatMap { it.toFlowable()
                        .map { it.id }
                        .toList()
                }.flatMap { playlistGateway.addSongsToPlaylist(playlist.id, it) }
                .map { Pair(it, playlist.title) }
    }
}