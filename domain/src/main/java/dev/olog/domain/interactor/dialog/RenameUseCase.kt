package dev.olog.domain.interactor.dialog

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.FolderGateway
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.shared.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class RenameUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val playlistGateway: PlaylistGateway,
        private val folderGateway: FolderGateway

) : CompletableUseCaseWithParam<Pair<MediaId, String>>(scheduler) {


    override fun buildUseCaseObservable(param: Pair<MediaId, String>): Completable {
        val (mediaId, newTitle) = param
        return when {
            mediaId.isFolder -> folderGateway.renameFolder(mediaId.categoryValue, newTitle)
            mediaId.isPlaylist -> playlistGateway.renamePlaylist(
                    mediaId.categoryValue.toLong(), newTitle)
            else -> Completable.complete()
        }
    }
}