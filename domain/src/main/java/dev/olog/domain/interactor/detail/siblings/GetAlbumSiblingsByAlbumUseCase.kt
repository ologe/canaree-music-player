package dev.olog.domain.interactor.detail.siblings

import dev.olog.domain.entity.Album
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.AlbumGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.domain.interactor.detail.item.GetAlbumUseCase
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject


class GetAlbumSiblingsByAlbumUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getAlbumUseCase: GetAlbumUseCase,
        private val albumGateway: AlbumGateway

) : FlowableUseCaseWithParam<List<Album>, String>(schedulers) {


    override fun buildUseCaseObservable(mediaId: String): Flowable<List<Album>> {
        val categoryValue = MediaIdHelper.extractCategoryValue(mediaId)
        val albumId = categoryValue.toLong()

        return getAlbumUseCase.execute(mediaId)
                .map{ it.artistId }
                .flatMap { artistId -> albumGateway.getAll().flatMapSingle {
                    it.toFlowable()
                            .filter { it.artistId == artistId }
                            .filter { it.id != albumId }
                            .toList()
                } }
                .map { if (it.size > 1) it else listOf() }
    }

}
