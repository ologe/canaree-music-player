package dev.olog.domain.interactor.detail.siblings

import dev.olog.domain.entity.Album
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.ArtistGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import javax.inject.Inject

class GetAlbumSiblingsByArtistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val artistGateway: ArtistGateway

) : FlowableUseCaseWithParam<List<Album>, String>(schedulers) {


    override fun buildUseCaseObservable(param: String): Flowable<List<Album>> {
        val categoryValue = MediaIdHelper.extractCategoryValue(param)
        val artistId = categoryValue.toLong()

        return artistGateway.getAlbums(artistId)
    }
}