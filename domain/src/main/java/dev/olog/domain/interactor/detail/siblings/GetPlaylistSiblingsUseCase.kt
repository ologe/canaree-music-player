package dev.olog.domain.interactor.detail.siblings

import dev.olog.domain.entity.Playlist
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaId
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class GetPlaylistSiblingsUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PlaylistGateway

) : FlowableUseCaseWithParam<List<Playlist>, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Flowable<List<Playlist>> {
        val playlistId = mediaId.categoryValue.toLong()

        return gateway.getAll()
                .flatMapSingle { it.toFlowable()
                        .filter { it.id != playlistId }
                        .toList()
                }
    }
}
