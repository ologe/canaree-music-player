package dev.olog.msc.domain.interactor.dialog

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.FolderGateway
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.msc.utils.MediaId
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
            else -> Completable.error(IllegalArgumentException("not a folder nor a playlist, $mediaId"))
        }
    }
}