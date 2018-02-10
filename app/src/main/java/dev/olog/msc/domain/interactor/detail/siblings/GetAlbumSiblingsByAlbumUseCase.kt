package dev.olog.msc.domain.interactor.detail.siblings

import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.ArtistGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.msc.domain.interactor.detail.item.GetAlbumUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject


class GetAlbumSiblingsByAlbumUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getAlbumUseCase: GetAlbumUseCase,
        private val artistGateway: ArtistGateway

) : FlowableUseCaseWithParam<List<Album>, MediaId>(schedulers) {


    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Flowable<List<Album>> {
        val albumId = mediaId.categoryValue.toLong()

        return getAlbumUseCase.execute(mediaId)
                .map{ it.artistId }
                .flatMap { artistGateway.getAlbums(it) }
                .flatMapSingle { it.toFlowable()
                        .filter { it.id != albumId }
                        .toList()
                }
    }

}
