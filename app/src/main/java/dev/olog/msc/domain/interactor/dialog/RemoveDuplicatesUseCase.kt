package dev.olog.msc.domain.interactor.dialog

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class RemoveDuplicatesUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: PlaylistGateway
): CompletableUseCaseWithParam<MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Completable {
        val playlistId = mediaId.resolveId
        return gateway.removeDuplicated(playlistId)
    }
}