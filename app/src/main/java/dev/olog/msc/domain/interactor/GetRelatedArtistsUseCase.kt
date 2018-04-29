package dev.olog.msc.domain.interactor

import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.domain.interactor.detail.item.GetArtistUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import io.reactivex.rxkotlin.toFlowable
import java.text.Collator
import javax.inject.Inject

class GetRelatedArtistsUseCase @Inject constructor(
        executors: ComputationScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getArtistUseCase: GetArtistUseCase,
        private val collator: Collator

) : ObservableUseCaseUseCaseWithParam<List<Artist>, MediaId>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Artist>> {
        if (!mediaId.isArtist && !mediaId.isAlbum){
            return getSongListByParamUseCase.execute(mediaId)
                    .flatMapSingle { it.toFlowable()
                            .filter { it.artist != AppConstants.UNKNOWN }
                            .distinct { it.artistId }
                            .map { MediaId.artistId(it.artistId) }
                            .flatMapSingle { getArtistUseCase.execute(it).firstOrError() }
                            .toList()
                    }.map {
                        it.sortedWith(Comparator { o1, o2 -> collator.compare(o1.name, o2.name) })
                    }
        } else return Observable.just(emptyList())
    }
}