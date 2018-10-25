package dev.olog.msc.domain.interactor.all.sorted

import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.entity.SortType
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.all.GetAllArtistsUseCase
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import dev.olog.msc.utils.safeCompare
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import java.text.Collator
import javax.inject.Inject

class GetAllArtistsSortedUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getAllUseCase: GetAllArtistsUseCase,
        private val appPrefsGateway: AppPreferencesGateway,
        private val collator: Collator

) : ObservableUseCase<List<Artist>>(schedulers){

    override fun buildUseCaseObservable(): Observable<List<Artist>> {
        return Observables.combineLatest(
                getAllUseCase.execute(),
                appPrefsGateway.observeAllArtistsSortOrder(),
                { tracks, order ->
                    val (sort, arranging) = order

                    if (arranging == SortArranging.ASCENDING){
                        tracks.sortedWith(getAscendingComparator(sort))
                    } else {
                        tracks.sortedWith(getDescendingComparator(sort))
                    }
                })
    }

    private fun getAscendingComparator(sortType: SortType): Comparator<Artist> {
        return when (sortType){
            SortType.ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o1.name, o2.name) }
            SortType.ALBUM_ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o1.albumArtist, o2.albumArtist) }
            else -> throw IllegalStateException("can't sort all artists, invalid sort type $sortType")
        }
    }

    private fun getDescendingComparator(sortType: SortType): Comparator<Artist> {
        return when (sortType){
            SortType.ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o2.name, o1.name) }
            SortType.ALBUM_ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o2.albumArtist, o1.albumArtist) }
            else -> throw IllegalStateException("can't sort all artists, invalid sort type $sortType")
        }
    }

}