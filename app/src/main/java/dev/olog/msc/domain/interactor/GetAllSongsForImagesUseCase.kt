package dev.olog.msc.domain.interactor

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.domain.interactor.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetAllSongsForImagesUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: SongGateway

) : SingleUseCase<List<Song>>(scheduler) {

    override fun buildUseCaseObservable(): Single<List<Song>> {
        return gateway.getAllForImageCreation()
    }
}