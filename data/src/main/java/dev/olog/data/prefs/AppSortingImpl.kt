package dev.olog.data.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.core.entity.LibrarySortType
import dev.olog.core.entity.SortArranging
import dev.olog.core.entity.SortType
import dev.olog.core.prefs.SortPreferences
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class AppSortingImpl @Inject constructor(
        private val preferences: SharedPreferences,
        private val rxPreferences: RxSharedPreferences

) : SortPreferences {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"

        private const val ALL_SONGS_SORT_ORDER = "$TAG.ALL_SONG_SORT_ORDER"
        private const val ALL_SONGS_SORT_ARRANGING = "$TAG.ALL_SONGS_SORT_ARRANGING"
        private const val ALL_ALBUMS_SORT_ORDER = "$TAG.ALL_ALBUMS_SORT_ORDER"
        private const val ALL_ALBUMS_SORT_ARRANGING = "$TAG.ALL_ALBUMS_SORT_ARRANGING"
        private const val ALL_ARTISTS_SORT_ORDER = "$TAG.ALL_ARTISTS_SORT_ORDER"
        private const val ALL_ARTISTS_SORT_ARRANGING = "$TAG.ALL_ARTISTS_SORT_ARRANGING"

        private const val DETAIL_SORT_FOLDER_ORDER = "$TAG.DETAIL_SORT_FOLDER_ORDER"
        private const val DETAIL_SORT_PLAYLIST_ORDER = "$TAG.DETAIL_SORT_PLAYLIST_ORDER"
        private const val DETAIL_SORT_ALBUM_ORDER = "$TAG.DETAIL_SORT_ALBUM_ORDER"
        private const val DETAIL_SORT_ARTIST_ORDER = "$TAG.DETAIL_SORT_ARTIST_ORDER"
        private const val DETAIL_SORT_GENRE_ORDER = "$TAG.DETAIL_SORT_GENRE_ORDER"

        private const val DETAIL_SORT_ARRANGING = "$TAG.DETAIL_SORT_ARRANGING"
    }

    override fun observeDetailFolderSortOrder(): Observable<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_FOLDER_ORDER, SortType.TITLE.ordinal)
                .asObservable()
                .map { ordinal -> SortType.values()[ordinal] }
    }

    override fun observeDetailPlaylistSortOrder(): Observable<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_PLAYLIST_ORDER, SortType.CUSTOM.ordinal)
                .asObservable()
                .map { ordinal -> SortType.values()[ordinal] }
    }

    override fun observeDetailAlbumSortOrder(): Observable<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_ALBUM_ORDER, SortType.TITLE.ordinal)
                .asObservable()
                .map { ordinal -> SortType.values()[ordinal] }
    }

    override fun observeDetailArtistSortOrder(): Observable<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_ARTIST_ORDER, SortType.TITLE.ordinal)
                .asObservable()
                .map { ordinal -> SortType.values()[ordinal] }
    }

    override fun observeDetailGenreSortOrder(): Observable<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_GENRE_ORDER, SortType.TITLE.ordinal)
                .asObservable()
                .map { ordinal -> SortType.values()[ordinal] }
    }

    override fun setDetailFolderSortOrder(sortType: SortType) : Completable{
        return Completable.fromCallable { preferences.edit { putInt(DETAIL_SORT_FOLDER_ORDER, sortType.ordinal) } }
    }

    override fun setDetailPlaylistSortOrder(sortType: SortType) : Completable{
        return Completable.fromCallable { preferences.edit { putInt(DETAIL_SORT_PLAYLIST_ORDER, sortType.ordinal) } }
    }

    override fun setDetailAlbumSortOrder(sortType: SortType) : Completable{
        return Completable.fromCallable { preferences.edit { putInt(DETAIL_SORT_ALBUM_ORDER, sortType.ordinal) } }
    }

    override fun setDetailArtistSortOrder(sortType: SortType) : Completable{
        return Completable.fromCallable { preferences.edit { putInt(DETAIL_SORT_ARTIST_ORDER, sortType.ordinal) } }
    }

    override fun setDetailGenreSortOrder(sortType: SortType) : Completable{
        return Completable.fromCallable { preferences.edit { putInt(DETAIL_SORT_GENRE_ORDER, sortType.ordinal) } }
    }

    override fun observeDetailSortArranging(): Observable<SortArranging> {
        return rxPreferences.getInteger(DETAIL_SORT_ARRANGING, SortArranging.ASCENDING.ordinal)
                .asObservable()
                .map { ordinal -> SortArranging.values()[ordinal] }
    }

    override fun toggleSortArranging() : Completable{
        val oldArranging = SortArranging.values()[preferences.getInt(DETAIL_SORT_ARRANGING, SortArranging.ASCENDING.ordinal)]

        val newArranging = if (oldArranging == SortArranging.ASCENDING){
            SortArranging.DESCENDING
        } else SortArranging.ASCENDING

        return Completable.fromCallable { preferences.edit { putInt(DETAIL_SORT_ARRANGING, newArranging.ordinal) } }
    }

    override fun getAllTracksSortOrder(): LibrarySortType {
        val sort = preferences.getInt(ALL_SONGS_SORT_ORDER, SortType.TITLE.ordinal)
        val arranging = preferences.getInt(ALL_SONGS_SORT_ARRANGING, SortArranging.ASCENDING.ordinal)
        return LibrarySortType(
            SortType.values()[sort],
            SortArranging.values()[arranging]
        )
    }

    override fun getAllAlbumsSortOrder(): LibrarySortType {
        val sort = preferences.getInt(ALL_ALBUMS_SORT_ORDER, SortType.TITLE.ordinal)
        val arranging = preferences.getInt(ALL_ALBUMS_SORT_ARRANGING, SortArranging.ASCENDING.ordinal)
        return LibrarySortType(
            SortType.values()[sort],
            SortArranging.values()[arranging]
        )
    }

    override fun getAllArtistsSortOrder(): LibrarySortType {
        val sort = preferences.getInt(ALL_ARTISTS_SORT_ORDER, SortType.ARTIST.ordinal)
        val arranging = preferences.getInt(ALL_ARTISTS_SORT_ARRANGING, SortArranging.ASCENDING.ordinal)
        return LibrarySortType(
            SortType.values()[sort],
            SortArranging.values()[arranging]
        )
    }

    override fun observeAllTracksSortOrder(): Observable<LibrarySortType> {
        return Observables.combineLatest(
                rxPreferences.getInteger(ALL_SONGS_SORT_ORDER, SortType.TITLE.ordinal).asObservable(),
                rxPreferences.getInteger(ALL_SONGS_SORT_ARRANGING, SortArranging.ASCENDING.ordinal).asObservable() //ascending default
        ) { sort, arranging ->
            LibrarySortType(
                SortType.values()[sort],
                SortArranging.values()[arranging]
            )
        }
    }

    override fun observeAllAlbumsSortOrder(): Observable<LibrarySortType> {
        return Observables.combineLatest(
                rxPreferences.getInteger(ALL_ALBUMS_SORT_ORDER, SortType.TITLE.ordinal).asObservable(),
                rxPreferences.getInteger(ALL_ALBUMS_SORT_ARRANGING, SortArranging.ASCENDING.ordinal).asObservable() //ascending default
        ) { sort, arranging ->
            LibrarySortType(
                SortType.values()[sort],
                SortArranging.values()[arranging]
            )
        }
    }

    override fun observeAllArtistsSortOrder(): Observable<LibrarySortType> {
        return Observables.combineLatest(
                rxPreferences.getInteger(ALL_ARTISTS_SORT_ORDER, SortType.ARTIST.ordinal).asObservable(),
                rxPreferences.getInteger(ALL_ARTISTS_SORT_ARRANGING, SortArranging.ASCENDING.ordinal).asObservable() //ascending default
        ) { sort, arranging ->
            LibrarySortType(
                SortType.values()[sort],
                SortArranging.values()[arranging]
            )
        }
    }

    override fun setAllTracksSortOrder(sortType: LibrarySortType) {
        preferences.edit {
            putInt(ALL_SONGS_SORT_ORDER, sortType.type.ordinal)
            putInt(ALL_SONGS_SORT_ARRANGING, sortType.arranging.ordinal)
        }
    }

    override fun setAllAlbumsSortOrder(sortType: LibrarySortType) {
        preferences.edit {
            putInt(ALL_ALBUMS_SORT_ORDER, sortType.type.ordinal)
            putInt(ALL_ALBUMS_SORT_ARRANGING, sortType.arranging.ordinal)
        }
    }

    override fun setAllArtistsSortOrder(sortType: LibrarySortType) {
        preferences.edit {
            putInt(ALL_ARTISTS_SORT_ORDER, sortType.type.ordinal)
            putInt(ALL_ARTISTS_SORT_ARRANGING, sortType.arranging.ordinal)
        }
    }

    override fun getDetailFolderSortOrder(): SortType {
        val ordinal = preferences.getInt(DETAIL_SORT_FOLDER_ORDER, SortType.TITLE.ordinal)
        return SortType.values()[ordinal]
    }

    override fun getDetailPlaylistSortOrder(): SortType {
        val ordinal = preferences.getInt(DETAIL_SORT_PLAYLIST_ORDER, SortType.CUSTOM.ordinal)
        return SortType.values()[ordinal]
    }

    override fun getDetailAlbumSortOrder(): SortType {
        val ordinal = preferences.getInt(DETAIL_SORT_ALBUM_ORDER, SortType.TITLE.ordinal)
        return SortType.values()[ordinal]
    }

    override fun getDetailArtistSortOrder(): SortType {
        val ordinal = preferences.getInt(DETAIL_SORT_ARTIST_ORDER, SortType.TITLE.ordinal)
        return SortType.values()[ordinal]
    }

    override fun getDetailGenreSortOrder(): SortType {
        val ordinal = preferences.getInt(DETAIL_SORT_GENRE_ORDER, SortType.TITLE.ordinal)
        return SortType.values()[ordinal]
    }

    override fun getDetailSortArranging(): SortArranging {
        val ordinal = preferences.getInt(DETAIL_SORT_ARRANGING, SortArranging.ASCENDING.ordinal)
        return SortArranging.values()[ordinal]
    }


}