package dev.olog.msc.domain.interactor.all.sibling

import dev.olog.msc.domain.entity.PodcastAlbum
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PodcastAlbumGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetPodcastAlbumSiblingsByArtistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val albumGateway: PodcastAlbumGateway

) : ObservableUseCaseUseCaseWithParam<List<PodcastAlbum>, MediaId>(schedulers) {


    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<PodcastAlbum>> {
        val artistId = mediaId.categoryValue.toLong()
        return albumGateway.observeByArtist(artistId)
    }
}