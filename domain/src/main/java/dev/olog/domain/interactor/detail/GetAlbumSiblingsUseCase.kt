package dev.olog.domain.interactor.detail

import dev.olog.domain.entity.Album
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.AlbumGateway
import dev.olog.domain.gateway.ArtistGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject


class GetAlbumSiblingsUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val artistGateway: ArtistGateway,
        private val albumGateway: AlbumGateway

) : FlowableUseCaseWithParam<List<Album>, String>(schedulers) {


    override fun buildUseCaseObservable(param: String): Flowable<List<Album>> {
        val categoryValue = MediaIdHelper.extractCategoryValue(param)
        val albumId = categoryValue.toLong()

        return albumGateway.getAll()
                .flatMap { it.toFlowable()
                        .filter { it.id == albumId }
                        .firstOrError()
                        .map { it.artistId }
                        .flatMapPublisher { artistGateway.getByParam(it) }
                        .map { it.id }
                }
                .flatMap { artistId -> albumGateway.getAll().flatMapSingle {
                    it.toFlowable()
                            .filter { it.artistId == artistId }
                            .toList()
                } }
    }

}
