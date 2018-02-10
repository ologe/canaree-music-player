package dev.olog.msc.domain.interactor.detail.siblings

import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.ArtistGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Flowable
import javax.inject.Inject

class GetAlbumSiblingsByArtistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val artistGateway: ArtistGateway

) : FlowableUseCaseWithParam<List<Album>, MediaId>(schedulers) {


    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Flowable<List<Album>> {
        val artistId = mediaId.categoryValue.toLong()
        return artistGateway.getAlbums(artistId)
    }
}