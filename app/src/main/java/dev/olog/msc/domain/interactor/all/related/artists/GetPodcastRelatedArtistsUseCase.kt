package dev.olog.msc.domain.interactor.all.related.artists

import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.PodcastArtist
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.domain.interactor.item.GetPodcastArtistUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import io.reactivex.rxkotlin.toFlowable
import java.text.Collator
import javax.inject.Inject

class GetPodcastRelatedArtistsUseCase @Inject constructor(
        private val executors: ComputationScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getPodcastArtistUseCase: GetPodcastArtistUseCase,
        private val collator: Collator

) : ObservableUseCaseUseCaseWithParam<List<PodcastArtist>, MediaId>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<PodcastArtist>> {
        if (mediaId.isPodcastPlaylist){
            return getSongListByParamUseCase.execute(mediaId)
                    .switchMapSingle { songList -> songList.toFlowable()
                            .filter { it.artist != AppConstants.UNKNOWN }
                            .distinct { it.artistId }
                            .map { MediaId.podcastArtistId(it.artistId) }
                            .flatMapSingle { getPodcastArtistUseCase.execute(it).firstOrError().subscribeOn(executors.worker) }
                            .toSortedList { o1, o2 -> collator.compare(o1.name, o2.name) }
                    }
        }
        return Observable.just(emptyList())
    }
}