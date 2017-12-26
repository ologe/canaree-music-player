package dev.olog.domain.interactor

import dev.olog.domain.entity.UneditedSong
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.SongGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import javax.inject.Inject

class GetUneditedSongUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: SongGateway

) : FlowableUseCaseWithParam<UneditedSong, String>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: String): Flowable<UneditedSong> {
        val leaf = MediaIdHelper.extractLeaf(mediaId)
        val songId = leaf.toLong()

        return gateway.getByParamUnedited(songId)
    }
}