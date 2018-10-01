package dev.olog.msc.domain.interactor.all.sibling

import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.AlbumGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.domain.interactor.item.GetAlbumUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import javax.inject.Inject


class GetAlbumSiblingsByAlbumUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getAlbumUseCase: GetAlbumUseCase,
        private val albumGateway: AlbumGateway

) : ObservableUseCaseWithParam<List<Album>, MediaId>(schedulers) {


    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Album>> {
        val albumId = mediaId.categoryValue.toLong()
        return getAlbumUseCase.execute(mediaId)
                .map { it.artistId }
                .flatMap { artistId ->
                    albumGateway.observeByArtist(artistId)
                            .map { it.filter { it.id != albumId } }
                }
    }

}
