package dev.olog.domain.interactor

import dev.olog.domain.entity.Song
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.SongGateway
import dev.olog.domain.interactor.base.SingleUseCase
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