package dev.olog.msc.domain.interactor.detail.item

import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Flowable
import javax.inject.Inject

class GetPlaylistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PlaylistGateway

) : FlowableUseCaseWithParam<Playlist, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Flowable<Playlist> {
        val playlistid = mediaId.categoryValue.toLong()

        return gateway.getByParam(playlistid)
    }
}
