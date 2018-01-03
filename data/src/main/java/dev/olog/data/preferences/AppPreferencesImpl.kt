package dev.olog.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.data.R
import dev.olog.data.utils.edit
import dev.olog.domain.SortArranging
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

        private const val DETAIL_SORT_FOLDER_ORDER = TAG + ".DETAIL_SORT_FOLDER_ORDER"
        private const val DETAIL_SORT_PLAYLIST_ORDER = TAG + ".DETAIL_SORT_PLAYLIST_ORDER"
        private const val DETAIL_SORT_ALBUM_ORDER = TAG + ".DETAIL_SORT_ALBUM_ORDER"
        private const val DETAIL_SORT_ARTIST_ORDER = TAG + ".DETAIL_SORT_ARTIST_ORDER"
        private const val DETAIL_SORT_GENRE_ORDER = TAG + ".DETAIL_SORT_GENRE_ORDER"

        private const val DETAIL_SORT_ARRANGING = TAG + ".DETAIL_SORT_ARRANGING"
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
}