package dev.olog.msc.domain.interactor.dialog

import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.msc.domain.interactor.item.GetSongUseCase
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Completable
import javax.inject.Inject

class AddToPlaylistUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val playlistGateway: PlaylistGateway,
        private val getSongUseCase: GetSongUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : CompletableUseCaseWithParam<Pair<Playlist, MediaId>>(scheduler) {

    override fun buildUseCaseObservable(param: Pair<Playlist, MediaId>): Completable {
        val (playlist, mediaId) = param

        if (mediaId.isLeaf) {
            val songId = mediaId.leaf!!
            return getSongUseCase.execute(mediaId)
                    .firstOrError()
                    .flatMapCompletable { playlistGateway.addSongsToPlaylist(playlist.id, listOf(songId)) }
        }

        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .mapToList { it.id }
                .flatMapCompletable { playlistGateway.addSongsToPlaylist(playlist.id, it) }
    }
}