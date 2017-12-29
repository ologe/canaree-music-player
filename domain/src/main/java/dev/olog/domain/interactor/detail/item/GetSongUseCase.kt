package dev.olog.domain.interactor.detail.item

import dev.olog.domain.entity.Song
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.SongGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaId
import io.reactivex.Flowable
import javax.inject.Inject

class GetSongUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: SongGateway

) : FlowableUseCaseWithParam<Song, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Flowable<Song> {
        val songId = mediaId.leaf!!
        return gateway.getByParam(songId)
    }
}
