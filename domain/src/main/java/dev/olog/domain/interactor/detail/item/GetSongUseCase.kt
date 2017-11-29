package dev.olog.domain.interactor.detail.item

import dev.olog.domain.entity.Song
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.SongGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import javax.inject.Inject

class GetSongUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: SongGateway

) : FlowableUseCaseWithParam<Song, String>(schedulers) {


    override fun buildUseCaseObservable(mediaId: String): Flowable<Song> {
        val categoryValue = MediaIdHelper.extractLeaf(mediaId)
        val songId = categoryValue.toLong()

        return gateway.getByParam(songId)
    }
}
