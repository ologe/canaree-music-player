package dev.olog.domain.interactor.dialog

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.shared.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class DeleteUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val playlistGateway: PlaylistGateway,
        private val songGateway: SongGateway,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : CompletableUseCaseWithParam<MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Completable {
        if (mediaId.isAll || mediaId.isLeaf) {
            return songGateway.deleteSingle(mediaId.leaf!!)
        }

        return when {
            mediaId.isPlaylist -> playlistGateway.deletePlaylist(mediaId.categoryValue.toLong())
            else -> getSongListByParamUseCase.execute(mediaId)
                    .flatMapCompletable { songGateway.deleteGroup(it) }
        }
    }
}