package dev.olog.domain.interactor.detail

import dev.olog.domain.entity.Artist
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.ArtistGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class GetArtistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: ArtistGateway

) : FlowableUseCaseWithParam<Artist, String>(schedulers) {


    override fun buildUseCaseObservable(param: String): Flowable<Artist> {
        val categoryValue = MediaIdHelper.extractCategoryValue(param)
        val artistId = categoryValue.toLong()

        return gateway.getAll()
                .flatMapSingle { it.toFlowable()
                        .filter { it.id == artistId }
                        .firstOrError()
                        .doOnSuccess { gateway.addLastPlayed(it) }
                }
    }
}
