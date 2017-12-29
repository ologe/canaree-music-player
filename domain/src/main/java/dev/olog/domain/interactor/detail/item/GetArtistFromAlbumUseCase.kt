package dev.olog.domain.interactor.detail.item

import dev.olog.domain.entity.Artist
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.AlbumGateway
import dev.olog.domain.gateway.ArtistGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaId
import io.reactivex.Flowable
import javax.inject.Inject

class GetArtistFromAlbumUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: ArtistGateway,
        private val albumGateway: AlbumGateway

) : FlowableUseCaseWithParam<Artist, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Flowable<Artist> {
        val albumId = mediaId.categoryValue.toLong()

        return albumGateway.getByParam(albumId)
                .flatMap { album -> gateway.getByParam(album.artistId) }
    }
}
