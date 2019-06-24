package dev.olog.msc.domain.interactor.all.related.artists

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.executor.ComputationScheduler
import dev.olog.core.gateway.PodcastPlaylistGateway2
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class GetPodcastRelatedArtistsUseCase @Inject constructor(
        executors: ComputationScheduler,
        private val playlistGateway2: PodcastPlaylistGateway2

) : ObservableUseCaseWithParam<List<Artist>, MediaId>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Artist>> {
        if (mediaId.isPodcastPlaylist) {
            return playlistGateway2.observeRelatedArtists(mediaId.categoryId).asObservable()
        }
        return Observable.just(emptyList())
    }
}