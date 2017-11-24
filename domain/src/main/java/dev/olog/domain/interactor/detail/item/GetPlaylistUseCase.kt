package dev.olog.domain.interactor.detail.item

import dev.olog.domain.entity.Playlist
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import javax.inject.Inject

class GetPlaylistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PlaylistGateway

) : FlowableUseCaseWithParam<Playlist, String>(schedulers) {


    override fun buildUseCaseObservable(mediaId: String): Flowable<Playlist> {
        val categoryValue = MediaIdHelper.extractCategoryValue(mediaId)
        val artistId = categoryValue.toLong()

        return gateway.getByParam(artistId)
    }
}
