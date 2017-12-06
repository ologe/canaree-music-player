package dev.olog.domain.interactor.detail.siblings

import dev.olog.domain.entity.Playlist
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class GetPlaylistSiblingsUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PlaylistGateway

) : FlowableUseCaseWithParam<List<Playlist>, String>(schedulers) {

    override fun buildUseCaseObservable(mediaId: String): Flowable<List<Playlist>> {
        val categoryValue = MediaIdHelper.extractCategoryValue(mediaId)
        val playlistId = categoryValue.toLong()

        return gateway.getAll()
                .flatMapSingle { it.toFlowable()
                        .filter { it.id != playlistId }
                        .toList()
                }
    }
}
