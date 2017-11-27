package dev.olog.domain.interactor.detail.siblings

import dev.olog.domain.entity.Album
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.AlbumGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class GetAlbumSiblingsByArtistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val albumGateway: AlbumGateway

) : FlowableUseCaseWithParam<List<Album>, String>(schedulers) {


    override fun buildUseCaseObservable(param: String): Flowable<List<Album>> {
        val categoryValue = MediaIdHelper.extractCategoryValue(param)
        val artistId = categoryValue.toLong()

        return albumGateway.getAll()
                .flatMapSingle { it.toFlowable()
                        .filter { it.artistId == artistId }
                        .toList()
                }.map { if (it.size > 1) it else listOf() }
    }
}