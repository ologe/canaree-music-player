package dev.olog.msc.domain.interactor

import dev.olog.msc.domain.entity.UneditedSong
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Flowable
import javax.inject.Inject

class GetUneditedSongUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: SongGateway

) : FlowableUseCaseWithParam<UneditedSong, MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Flowable<UneditedSong> {
        val songId = mediaId.leaf
        return gateway.getByParamUnedited(songId!!)
    }
}