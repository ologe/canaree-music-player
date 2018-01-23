package dev.olog.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.data.R
import dev.olog.data.utils.edit
import dev.olog.domain.entity.LibraryCategoryBehavior
import dev.olog.domain.entity.SortArranging
import dev.olog.domain.entity.SortType
import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.shared.ApplicationContext
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesImpl @Inject constructor(
        @ApplicationContext private val context: Context,
        private val preferences: SharedPreferences,
        private val rxPreferences: RxSharedPreferences

) : AppPreferencesGateway {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"
        private const val FIRST_ACCESS = TAG + ".FIRST_ACCESS"

        private const val VIEW_PAGER_LAST_PAGE = TAG + ".VIEW_PAGER_LAST_PAGE"

        private const val NEXT_SLEEP = TAG + ".NEXT_SLEEP"

        private const val DETAIL_SORT_FOLDER_ORDER = TAG + ".DETAIL_SORT_FOLDER_ORDER"
        private const val DETAIL_SORT_PLAYLIST_ORDER = TAG + ".DETAIL_SORT_PLAYLIST_ORDER"
        private const val DETAIL_SORT_ALBUM_ORDER = TAG + ".DETAIL_SORT_ALBUM_ORDER"
        private const val DETAIL_SORT_ARTIST_ORDER = TAG + ".DETAIL_SORT_ARTIST_ORDER"
        private const val DETAIL_SORT_GENRE_ORDER = TAG + ".DETAIL_SORT_GENRE_ORDER"

        private const val DETAIL_SORT_ARRANGING = TAG + ".DETAIL_SORT_ARRANGING"

        private const val CATEGORY_FOLDER_ORDER = TAG + ".CATEGORY_FOLDER_ORDER"
        private const val CATEGORY_PLAYLIST_ORDER = TAG + ".CATEGORY_PLAYLIST_ORDER"
        private const val CATEGORY_SONG_ORDER = TAG + ".CATEGORY_SONG_ORDER"
        private const val CATEGORY_ALBUM_ORDER = TAG + ".CATEGORY_ALBUM_ORDER"
        private const val CATEGORY_ARTIST_ORDER = TAG + ".CATEGORY_ARTIST_ORDER"
        private const val CATEGORY_GENRE_ORDER = TAG + ".CATEGORY_GENRE_ORDER"

        private const val CATEGORY_FOLDER_VISIBILITY = TAG + ".CATEGORY_FOLDER_VISIBILITY"
        private const val CATEGORY_PLAYLIST_VISIBILITY = TAG + ".CATEGORY_PLAYLIST_VISIBILITY"
        private const val CATEGORY_SONG_VISIBILITY = TAG + ".CATEGORY_SONG_VISIBILITY"
        private const val CATEGORY_ALBUM_VISIBILITY = TAG + ".CATEGORY_ALBUM_VISIBILITY"
        private const val CATEGORY_ARTIST_VISIBILITY = TAG + ".CATEGORY_ARTIST_VISIBILITY"
        private const val CATEGORY_GENRE_VISIBILITY = TAG + ".CATEGORY_GENRE_VISIBILITY"

        private const val BLACKLIST = TAG + ".BLACKLIST"
    }

    override fun isFirstAccess(): Boolean {
        val isFirstAccess = preferences.getBoolean(FIRST_ACCESS, true)

        if (isFirstAccess) {
            preferences.edit { putBoolean(FIRST_ACCESS, false) }
        }

        return isFirstAccess
    }

    override fun getViewPagerLastVisitedPage(): Int {
        val remember = preferences.getBoolean(context.getString(R.string.prefs_remember_last_tab_key), true)
        if (remember){
            return preferences.getInt(VIEW_PAGER_LAST_PAGE, 2)
        }
        return 2
    }

    override fun setViewPagerLastVisitedPage(lastPage: Int) {
        preferences.edit { putInt(VIEW_PAGER_LAST_PAGE, lastPage) }
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

    override fun getVisibleTabs(): Flowable<BooleanArray> {
        return rxPreferences.getStringSet(context.getString(R.string.prefs_detail_visible_items_key))
                .asObservable()
                .toFlowable(BackpressureStrategy.LATEST)
                .map {
                    booleanArrayOf(
                            it.contains(context.getString(R.string.prefs_detail_visible_tabs_most_played)),
                            it.contains(context.getString(R.string.prefs_detail_visible_tabs_recently_added)),
                            it.contains(context.getString(R.string.prefs_detail_visible_tabs_related_artists))
                    )
                }
    }

    override fun isIconsDark(): Flowable<Boolean> {
        return rxPreferences.getBoolean(context.getString(R.string.prefs_icon_color_key), true)
                .asObservable().toFlowable(BackpressureStrategy.LATEST)
    }

    override fun getLowerVolumeOnNight(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_lower_volume_key), false)
    }

    override fun observeLowerVolumeOnNight(): Flowable<Boolean> {
        return rxPreferences.getBoolean(context.getString(R.string.prefs_lower_volume_key), false)
                .asObservable().toFlowable(BackpressureStrategy.LATEST)
    }

    override fun getLibraryCategoriesBehavior(): List<LibraryCategoryBehavior> {
        val categories = context.resources.getStringArray(R.array.categories)

        return listOf(
                LibraryCategoryBehavior(
                        categories[0],
                        preferences.getBoolean(CATEGORY_FOLDER_VISIBILITY, true),
                        preferences.getInt(CATEGORY_FOLDER_ORDER, 0)
                ),
                LibraryCategoryBehavior(
                        categories[1],
                        preferences.getBoolean(CATEGORY_PLAYLIST_VISIBILITY, true),
                        preferences.getInt(CATEGORY_PLAYLIST_ORDER, 1)
                ),
                LibraryCategoryBehavior(
                        categories[2],
                        preferences.getBoolean(CATEGORY_SONG_VISIBILITY, true),
                        preferences.getInt(CATEGORY_SONG_ORDER, 2)
                ),
                LibraryCategoryBehavior(
                        categories[3],
                        preferences.getBoolean(CATEGORY_ALBUM_VISIBILITY, true),
                        preferences.getInt(CATEGORY_ALBUM_ORDER, 3)
                ),
                LibraryCategoryBehavior(
                        categories[4],
                        preferences.getBoolean(CATEGORY_ARTIST_VISIBILITY, true),
                        preferences.getInt(CATEGORY_ARTIST_ORDER, 4)
                ),
                LibraryCategoryBehavior(
                        categories[5],
                        preferences.getBoolean(CATEGORY_GENRE_VISIBILITY, true),
                        preferences.getInt(CATEGORY_GENRE_ORDER, 5)
                )
        ).sortedBy { it.order }
    }

    override fun getDefaultLibraryCategoriesBehavior(): List<LibraryCategoryBehavior> {
        val categories = context.resources.getStringArray(R.array.categories)

        return categories.mapIndexed { index, category ->
            LibraryCategoryBehavior(category, true, index)
        }
    }

    override fun setLibraryCategoriesBehavior(behavior: List<LibraryCategoryBehavior>) {
        preferences.edit {
            val folder = behavior.first { it.category == context.getString(R.string.category_folders) }
            putInt(CATEGORY_FOLDER_ORDER, folder.order)
            putBoolean(CATEGORY_FOLDER_VISIBILITY, folder.enabled)

            val playlist = behavior.first { it.category == context.getString(R.string.category_playlists) }
            putInt(CATEGORY_PLAYLIST_ORDER, playlist.order)
            putBoolean(CATEGORY_PLAYLIST_VISIBILITY, playlist.enabled)

            val song = behavior.first { it.category == context.getString(R.string.category_songs) }
            putInt(CATEGORY_SONG_ORDER, song.order)
            putBoolean(CATEGORY_SONG_VISIBILITY, song.enabled)

            val album = behavior.first { it.category == context.getString(R.string.category_albums) }
            putInt(CATEGORY_ALBUM_ORDER, album.order)
            putBoolean(CATEGORY_ALBUM_VISIBILITY, album.enabled)

            val artist = behavior.first { it.category == context.getString(R.string.category_artists) }
            putInt(CATEGORY_ARTIST_ORDER, artist.order)
            putBoolean(CATEGORY_ARTIST_VISIBILITY, artist.enabled)

            val genre = behavior.first { it.category == context.getString(R.string.category_genres) }
            putInt(CATEGORY_GENRE_ORDER, genre.order)
            putBoolean(CATEGORY_GENRE_VISIBILITY, genre.enabled)
        }
    }

    override fun getBlackList(): Set<String> {
        return preferences.getStringSet(BLACKLIST, setOf())
    }

    override fun setBlackList(set: Set<String>) {
        preferences.edit { putStringSet(BLACKLIST, set) }
    }

    override fun resetSleepTimer() {
        setSleepTimer(-1L)
    }

    override fun setSleepTimer(millis: Long) {
        preferences.edit { putLong(NEXT_SLEEP, millis) }
    }

    override fun getSleepTimer(): Long {
        return preferences.getLong(NEXT_SLEEP, -1L)
    }
}