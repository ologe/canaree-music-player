package dev.olog.domain.interactor.dialog

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.base.SingleUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class AddToPlaylistUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val playlistGateway: PlaylistGateway,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : SingleUseCaseWithParam<String, Pair<Long, String>>(scheduler) {

    override fun buildUseCaseObservable(param: Pair<Long, String>): Single<String> {
        val (playlistId, mediaId) = param
        val category = MediaIdHelper.extractCategory(mediaId)

        return when (category) {
            MediaIdHelper.MEDIA_ID_BY_ALL -> {
                val songId = MediaIdHelper.extractLeaf(mediaId).toLong()
                playlistGateway.addSongsToPlaylist(playlistId, listOf(songId))
            }
            else -> getSongListByParamUseCase.execute(mediaId)
                    .firstOrError()
                    .flatMap { it.toFlowable()
                            .map { it.id }
                            .toList()
                    }.flatMap { playlistGateway.addSongsToPlaylist(playlistId, it) }
        }
    }
}