package dev.olog.msc.domain.interactor.all.sibling

import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.AlbumGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetAlbumSiblingsByArtistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val albumGateway: AlbumGateway

) : ObservableUseCaseUseCaseWithParam<List<Album>, MediaId>(schedulers) {


    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Album>> {
        val artistId = mediaId.categoryValue.toLong()
        return albumGateway.observeByArtist(artistId)
    }
}