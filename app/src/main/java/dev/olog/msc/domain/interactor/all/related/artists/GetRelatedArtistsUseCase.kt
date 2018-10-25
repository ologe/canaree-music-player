package dev.olog.msc.domain.interactor.all.related.artists

import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.executors.ComputationScheduler
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.domain.interactor.item.GetArtistUseCase
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.safeCompare
import io.reactivex.Observable
import io.reactivex.rxkotlin.toFlowable
import java.text.Collator
import javax.inject.Inject

class GetRelatedArtistsUseCase @Inject constructor(
        private val executors: ComputationScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getArtistUseCase: GetArtistUseCase,
        private val collator: Collator

) : ObservableUseCaseWithParam<List<Artist>, MediaId>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Artist>> {
        if (mediaId.isFolder || mediaId.isPlaylist || mediaId.isGenre){
            return getSongListByParamUseCase.execute(mediaId)
                    .switchMapSingle { songList -> songList.toFlowable()
                            .filter { it.artist != AppConstants.UNKNOWN }
                            .distinct { it.artistId }
                            .map { MediaId.artistId(it.artistId) }
                            .flatMapSingle { getArtistUseCase.execute(it).firstOrError().subscribeOn(executors.worker) }
                            .toSortedList { o1, o2 -> collator.safeCompare(o1.name, o2.name) }
                    }
        }
        return Observable.just(emptyList())
    }
}