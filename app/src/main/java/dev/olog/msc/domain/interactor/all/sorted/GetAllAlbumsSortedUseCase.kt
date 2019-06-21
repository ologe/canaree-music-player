package dev.olog.msc.domain.interactor.all.sorted

import dev.olog.core.entity.Album
import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.entity.SortType
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.all.GetAllAlbumsUseCase
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import dev.olog.msc.utils.safeCompare
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import java.text.Collator
import javax.inject.Inject

class GetAllAlbumsSortedUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val getAllUseCase: GetAllAlbumsUseCase,
        private val appPrefsGateway: AppPreferencesGateway,
        private val collator: Collator

) : ObservableUseCase<List<Album>>(schedulers){

    override fun buildUseCaseObservable(): Observable<List<Album>> {
        return Observables.combineLatest(
                getAllUseCase.execute(),
                appPrefsGateway.observeAllAlbumsSortOrder(),
                { tracks, order ->
                    val (sort, arranging) = order

                    if (arranging == SortArranging.ASCENDING){
                        tracks.sortedWith(getAscendingComparator(sort))
                    } else {
                        tracks.sortedWith(getDescendingComparator(sort))
                    }
                })
    }

    private fun getAscendingComparator(sortType: SortType): Comparator<Album> {
        return when (sortType){
            SortType.TITLE -> Comparator { o1, o2 -> collator.safeCompare(o1.title, o2.title) }
            SortType.ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o1.artist, o2.artist) }
            SortType.ALBUM_ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o1.albumArtist, o2.albumArtist) }
            else -> throw IllegalStateException("can't sort all albums, invalid sort type $sortType")
        }
    }

    private fun getDescendingComparator(sortType: SortType): Comparator<Album> {
        return when (sortType){
            SortType.TITLE -> Comparator { o1, o2 -> collator.safeCompare(o2.title, o1.title) }
            SortType.ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o2.artist, o1.artist) }
            SortType.ALBUM_ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o2.albumArtist, o1.albumArtist) }
            else -> throw IllegalStateException("can't sort all albums, invalid sort type $sortType")
        }
    }

}