package dev.olog.data.preferences

import android.content.SharedPreferences
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.data.utils.edit
import dev.olog.domain.SortArranging
import dev.olog.domain.entity.SortType
import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesImpl @Inject constructor(
        private val preferences: SharedPreferences,
        private val rxPreferences: RxSharedPreferences

) : AppPreferencesGateway {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"
        private const val FIRST_ACCESS = TAG + ".FIRST_ACCESS"

        private const val DETAIL_SORT_FOLDER_ORDER = TAG + ".DETAIL_SORT_FOLDER_ORDER"
        private const val DETAIL_SORT_PLAYLIST_ORDER = TAG + ".DETAIL_SORT_PLAYLIST_ORDER"
        private const val DETAIL_SORT_ALBUM_ORDER = TAG + ".DETAIL_SORT_ALBUM_ORDER"
        private const val DETAIL_SORT_ARTIST_ORDER = TAG + ".DETAIL_SORT_ARTIST_ORDER"
        private const val DETAIL_SORT_GENRE_ORDER = TAG + ".DETAIL_SORT_GENRE_ORDER"

        private const val DETAIL_SORT_ARRANGING = TAG + ".DETAIL_SORT_ARRANGING"
        private const val DETAIL_VISIBILITY_MOST_PLAYED = TAG + ".DETAIL_VISIBILITY_MOST_PLAYED"
        private const val DETAIL_VISIBILITY_RECENTLY_ADDED = TAG + ".DETAIL_VISIBILITY_RECENTLY_ADDED"
        private const val DETAIL_VISIBILITY_RELATED_ARTISTS = TAG + ".DETAIL_VISIBILITY_RELATED_ARTISTS"
    }

    override fun isFirstAccess(): Boolean {
        val isFirstAccess = preferences.getBoolean(FIRST_ACCESS, true)

        if (isFirstAccess) {
            preferences.edit { putBoolean(FIRST_ACCESS, false) }
        }

        return isFirstAccess
    }

    override fun getFolderSortOrder(): Flowable<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_FOLDER_ORDER, SortType.TITLE.ordinal)
                .asObservable()
                .toFlowable(BackpressureStrategy.LATEST)
                .map { ordinal -> SortType.values()[ordinal] }
    }

    override fun getPlaylistSortOrder(): Flowable<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_PLAYLIST_ORDER, SortType.CUSTOM.ordinal)
                .asObservable()
                .toFlowable(BackpressureStrategy.LATEST)
                .map { ordinal -> SortType.values()[ordinal] }
    }

    override fun getAlbumSortOrder(): Flowable<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_ALBUM_ORDER, SortType.TITLE.ordinal)
                .asObservable()
                .toFlowable(BackpressureStrategy.LATEST)
                .map { ordinal -> SortType.values()[ordinal] }
    }

    override fun getArtistSortOrder(): Flowable<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_ARTIST_ORDER, SortType.TITLE.ordinal)
                .asObservable()
                .toFlowable(BackpressureStrategy.LATEST)
                .map { ordinal -> SortType.values()[ordinal] }
    }

    override fun getGenreSortOrder(): Flowable<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_GENRE_ORDER, SortType.TITLE.ordinal)
                .asObservable()
                .toFlowable(BackpressureStrategy.LATEST)
                .map { ordinal -> SortType.values()[ordinal] }
    }

    override fun setFolderSortOrder(sortType: SortType) : Completable{
        return Completable.fromCallable { preferences.edit { putInt(DETAIL_SORT_FOLDER_ORDER, sortType.ordinal) } }
    }

    override fun setPlaylistSortOrder(sortType: SortType) : Completable{
        return Completable.fromCallable { preferences.edit { putInt(DETAIL_SORT_PLAYLIST_ORDER, sortType.ordinal) } }
    }

    override fun setAlbumSortOrder(sortType: SortType) : Completable{
        return Completable.fromCallable { preferences.edit { putInt(DETAIL_SORT_ALBUM_ORDER, sortType.ordinal) } }
    }

    override fun setArtistSortOrder(sortType: SortType) : Completable{
        return Completable.fromCallable { preferences.edit { putInt(DETAIL_SORT_ARTIST_ORDER, sortType.ordinal) } }
    }

    override fun setGenreSortOrder(sortType: SortType) : Completable{
        return Completable.fromCallable { preferences.edit { putInt(DETAIL_SORT_GENRE_ORDER, sortType.ordinal) } }
    }

    override fun getSortArranging(): Flowable<SortArranging> {
        return rxPreferences.getInteger(DETAIL_SORT_ARRANGING, SortArranging.ASCENDING.ordinal)
                .asObservable()
                .toFlowable(BackpressureStrategy.LATEST)
                .map { ordinal -> SortArranging.values()[ordinal] }
    }

    override fun toggleSortArranging() : Completable{
        val oldArranging = SortArranging.values()[preferences.getInt(DETAIL_SORT_ARRANGING, SortArranging.ASCENDING.ordinal)]

        val newArranging = if (oldArranging == SortArranging.ASCENDING){
            SortArranging.DESCENDING
        } else SortArranging.ASCENDING

        return Completable.fromCallable { preferences.edit { putInt(DETAIL_SORT_ARRANGING, newArranging.ordinal) } }
    }

    override fun observeVisibleTabs(): Flowable<List<Boolean>> {
        val mostPlayedVisibility = rxPreferences.getBoolean(DETAIL_VISIBILITY_MOST_PLAYED, true).asObservable()
        val recentlyAddedVisibility = rxPreferences.getBoolean(DETAIL_VISIBILITY_RECENTLY_ADDED, true).asObservable()
        val relatedArtistsVisibility = rxPreferences.getBoolean(DETAIL_VISIBILITY_RELATED_ARTISTS, true).asObservable()
        return Observables.combineLatest(
                mostPlayedVisibility,
                recentlyAddedVisibility,
                relatedArtistsVisibility, { mostPlayed, recentlyAdded, relatedArtists ->

            listOf(mostPlayed, recentlyAdded, relatedArtists)
        }
        ).toFlowable(BackpressureStrategy.LATEST)
    }

    override fun getVisibleTabs(): BooleanArray {
        val mostPlayedVisibility = preferences.getBoolean(DETAIL_VISIBILITY_MOST_PLAYED, true)
        val recentlyAddedVisibility = preferences.getBoolean(DETAIL_VISIBILITY_RECENTLY_ADDED, true)
        val relatedArtistsVisibility = preferences.getBoolean(DETAIL_VISIBILITY_RELATED_ARTISTS, true)
        return booleanArrayOf(mostPlayedVisibility, recentlyAddedVisibility, relatedArtistsVisibility)
    }

    override fun setVisibleTabs(items: List<Boolean>) {
        preferences.edit {
            putBoolean(DETAIL_VISIBILITY_MOST_PLAYED, items[0])
            putBoolean(DETAIL_VISIBILITY_RECENTLY_ADDED, items[1])
            putBoolean(DETAIL_VISIBILITY_RELATED_ARTISTS, items[2])
        }
    }
}