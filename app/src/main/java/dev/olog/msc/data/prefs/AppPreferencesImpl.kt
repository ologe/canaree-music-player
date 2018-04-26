package dev.olog.msc.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.LibraryCategoryBehavior
import dev.olog.msc.domain.entity.LibrarySortType
import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.entity.SortType
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.utils.MediaIdCategory
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class AppPreferencesImpl @Inject constructor(
        @ApplicationContext private val context: Context,
        private val preferences: SharedPreferences,
        private val rxPreferences: RxSharedPreferences

) : AppPreferencesGateway {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"
        private const val FIRST_ACCESS = "$TAG.FIRST_ACCESS"

        private const val VIEW_PAGER_LAST_PAGE = "$TAG.VIEW_PAGER_LAST_PAGE"

        private const val SLEEP_TIME = "$TAG.SLEEP_TIME"
        private const val SLEEP_FROM = "$TAG.FROM_WHEN"

        private const val ALL_SONGS_SORT_ORDER = "$TAG.ALL_SONG_SORT_ORDER"
        private const val ALL_ALBUMS_SORT_ORDER = "$TAG.ALL_ALBUMS_SORT_ORDER"
        private const val ALL_ARTIST_SORT_ORDER = "$TAG.ALL_ARTIST_SORT_ORDER"

        private const val DETAIL_SORT_FOLDER_ORDER = "$TAG.DETAIL_SORT_FOLDER_ORDER"
        private const val DETAIL_SORT_PLAYLIST_ORDER = "$TAG.DETAIL_SORT_PLAYLIST_ORDER"
        private const val DETAIL_SORT_ALBUM_ORDER = "$TAG.DETAIL_SORT_ALBUM_ORDER"
        private const val DETAIL_SORT_ARTIST_ORDER = "$TAG.DETAIL_SORT_ARTIST_ORDER"
        private const val DETAIL_SORT_GENRE_ORDER = "$TAG.DETAIL_SORT_GENRE_ORDER"

        private const val DETAIL_SORT_ARRANGING = "$TAG.DETAIL_SORT_ARRANGING"

        private const val CATEGORY_FOLDER_ORDER = "$TAG.CATEGORY_FOLDER_ORDER"
        private const val CATEGORY_PLAYLIST_ORDER = "$TAG.CATEGORY_PLAYLIST_ORDER"
        private const val CATEGORY_SONG_ORDER = "$TAG.CATEGORY_SONG_ORDER"
        private const val CATEGORY_ALBUM_ORDER = "$TAG.CATEGORY_ALBUM_ORDER"
        private const val CATEGORY_ARTIST_ORDER = "$TAG.CATEGORY_ARTIST_ORDER"
        private const val CATEGORY_GENRE_ORDER = "$TAG.CATEGORY_GENRE_ORDER"

        private const val CATEGORY_FOLDER_VISIBILITY = "$TAG.CATEGORY_FOLDER_VISIBILITY"
        private const val CATEGORY_PLAYLIST_VISIBILITY = "$TAG.CATEGORY_PLAYLIST_VISIBILITY"
        private const val CATEGORY_SONG_VISIBILITY = "$TAG.CATEGORY_SONG_VISIBILITY"
        private const val CATEGORY_ALBUM_VISIBILITY = "$TAG.CATEGORY_ALBUM_VISIBILITY"
        private const val CATEGORY_ARTIST_VISIBILITY = "$TAG.CATEGORY_ARTIST_VISIBILITY"
        private const val CATEGORY_GENRE_VISIBILITY = "$TAG.CATEGORY_GENRE_VISIBILITY"

        private const val BLACKLIST = "$TAG.BLACKLIST"
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

    override fun getFolderSortOrder(): Observable<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_FOLDER_ORDER, SortType.TITLE.ordinal)
                .asObservable()
                .map { ordinal -> SortType.values()[ordinal] }
    }

    override fun getPlaylistSortOrder(): Observable<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_PLAYLIST_ORDER, SortType.CUSTOM.ordinal)
                .asObservable()
                .map { ordinal -> SortType.values()[ordinal] }
    }

    override fun getAlbumSortOrder(): Observable<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_ALBUM_ORDER, SortType.TITLE.ordinal)
                .asObservable()
                .map { ordinal -> SortType.values()[ordinal] }
    }

    override fun getArtistSortOrder(): Observable<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_ARTIST_ORDER, SortType.TITLE.ordinal)
                .asObservable()
                .map { ordinal -> SortType.values()[ordinal] }
    }

    override fun getGenreSortOrder(): Observable<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_GENRE_ORDER, SortType.TITLE.ordinal)
                .asObservable()
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

    override fun getSortArranging(): Observable<SortArranging> {
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

    override fun getVisibleTabs(): Observable<BooleanArray> {
        return rxPreferences.getStringSet(context.getString(R.string.prefs_detail_sections_key))
                .asObservable()
                .map {
                    booleanArrayOf(
                            it.contains(context.getString(R.string.prefs_detail_section_entry_value_most_played)),
                            it.contains(context.getString(R.string.prefs_detail_section_entry_value_recently_added)),
                            it.contains(context.getString(R.string.prefs_detail_section_entry_value_related_artists))
                    )
                }
    }

    override fun getLibraryCategories(): List<LibraryCategoryBehavior> {
        return listOf(
                LibraryCategoryBehavior(
                        MediaIdCategory.FOLDERS,
                        preferences.getBoolean(CATEGORY_FOLDER_VISIBILITY, true),
                        preferences.getInt(CATEGORY_FOLDER_ORDER, 0)
                ),
                LibraryCategoryBehavior(
                        MediaIdCategory.PLAYLISTS,
                        preferences.getBoolean(CATEGORY_PLAYLIST_VISIBILITY, true),
                        preferences.getInt(CATEGORY_PLAYLIST_ORDER, 1)
                ),
                LibraryCategoryBehavior(
                        MediaIdCategory.SONGS,
                        preferences.getBoolean(CATEGORY_SONG_VISIBILITY, true),
                        preferences.getInt(CATEGORY_SONG_ORDER, 2)
                ),
                LibraryCategoryBehavior(
                        MediaIdCategory.ALBUMS,
                        preferences.getBoolean(CATEGORY_ALBUM_VISIBILITY, true),
                        preferences.getInt(CATEGORY_ALBUM_ORDER, 3)
                ),
                LibraryCategoryBehavior(
                        MediaIdCategory.ARTISTS,
                        preferences.getBoolean(CATEGORY_ARTIST_VISIBILITY, true),
                        preferences.getInt(CATEGORY_ARTIST_ORDER, 4)
                ),
                LibraryCategoryBehavior(
                        MediaIdCategory.GENRES,
                        preferences.getBoolean(CATEGORY_GENRE_VISIBILITY, true),
                        preferences.getInt(CATEGORY_GENRE_ORDER, 5)
                )
        ).sortedBy { it.order }
    }

    override fun getDefaultLibraryCategories(): List<LibraryCategoryBehavior> {
        return MediaIdCategory.values()
                .take(6)
                .mapIndexed { index, category -> LibraryCategoryBehavior(category, true, index) }
    }

    override fun setLibraryCategories(behavior: List<LibraryCategoryBehavior>) {
        preferences.edit {
            val folder = behavior.first { it.category == MediaIdCategory.FOLDERS }
            putInt(CATEGORY_FOLDER_ORDER, folder.order)
            putBoolean(CATEGORY_FOLDER_VISIBILITY, folder.visible)

            val playlist = behavior.first { it.category == MediaIdCategory.PLAYLISTS }
            putInt(CATEGORY_PLAYLIST_ORDER, playlist.order)
            putBoolean(CATEGORY_PLAYLIST_VISIBILITY, playlist.visible)

            val song = behavior.first { it.category == MediaIdCategory.SONGS }
            putInt(CATEGORY_SONG_ORDER, song.order)
            putBoolean(CATEGORY_SONG_VISIBILITY, song.visible)

            val album = behavior.first { it.category == MediaIdCategory.ALBUMS }
            putInt(CATEGORY_ALBUM_ORDER, album.order)
            putBoolean(CATEGORY_ALBUM_VISIBILITY, album.visible)

            val artist = behavior.first { it.category == MediaIdCategory.ARTISTS }
            putInt(CATEGORY_ARTIST_ORDER, artist.order)
            putBoolean(CATEGORY_ARTIST_VISIBILITY, artist.visible)

            val genre = behavior.first { it.category == MediaIdCategory.GENRES }
            putInt(CATEGORY_GENRE_ORDER, genre.order)
            putBoolean(CATEGORY_GENRE_VISIBILITY, genre.visible)
        }
    }

    override fun getBlackList(): Set<String> {
        return preferences.getStringSet(BLACKLIST, setOf())
    }

    override fun setBlackList(set: Set<String>) {
        preferences.edit { putStringSet(BLACKLIST, set) }
    }

    override fun resetSleepTimer() {
        setSleepTimer(-1L, -1L)
    }

    override fun setSleepTimer(sleepFrom: Long, sleepTime: Long) {
        preferences.edit {
            putLong(SLEEP_FROM, sleepFrom)
            putLong(SLEEP_TIME, sleepTime)
        }
    }

    override fun getSleepTime(): Long {
        return preferences.getLong(SLEEP_TIME, -1)
    }

    override fun getSleepFrom(): Long {
        return preferences.getLong(SLEEP_FROM, -1)
    }

    override fun observePlayerControlsVisibility(): Observable<Boolean> {
        val key = context.getString(R.string.prefs_player_controls_visibility_key)
        return rxPreferences.getBoolean(key, false)
                .asObservable()
    }

    override fun setDefault(): Completable {
        return Completable.create { emitter ->
            setLibraryCategories(getDefaultLibraryCategories())
            setBlackList(setOf())
            hideQuickAction()
            setDefaultVisibleSections()
            hideClassicPlayerControls()
            setDefaultAutoDownloadImages()
            setDefaultTheme()

            emitter.onComplete()
        }
    }

    private fun setDefaultAutoDownloadImages(){
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_auto_download_images_key), false)
        }
    }

    private fun hideQuickAction(){
        preferences.edit {
            putString(context.getString(R.string.prefs_quick_action_key), context.getString(R.string.prefs_quick_action_entry_value_hide))
        }
    }

    private fun setDefaultVisibleSections(){
        preferences.edit {
            val default = context.resources.getStringArray(R.array.prefs_detail_sections_entry_values_default).toSet()
            putStringSet(context.getString(R.string.prefs_detail_sections_key), default)
        }
    }

    private fun hideClassicPlayerControls(){
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_player_controls_visibility_key), false)
        }
    }

    private fun setDefaultTheme(){
        preferences.edit {
            putString(context.getString(R.string.prefs_appearance_key), context.getString(R.string.prefs_appearance_entry_value_default))
        }
    }


    override fun getAllTracksSortOrder(): Observable<LibrarySortType> {
        return rxPreferences.getInteger(ALL_SONGS_SORT_ORDER, LibrarySortType.TITLE_AZ.ordinal)
                .asObservable()
                .map { ordinal -> LibrarySortType.values()[ordinal] }
    }

    override fun getAllAlbumsSortOrder(): Observable<LibrarySortType> {
        return rxPreferences.getInteger(ALL_ALBUMS_SORT_ORDER, LibrarySortType.ALBUM_AZ.ordinal)
                .asObservable()
                .map { ordinal -> LibrarySortType.values()[ordinal] }
    }

    override fun getAllArtistSortOrder(): Observable<LibrarySortType> {
        return rxPreferences.getInteger(ALL_ARTIST_SORT_ORDER, LibrarySortType.ARTIST_AZ.ordinal)
                .asObservable()
                .map { ordinal -> LibrarySortType.values()[ordinal] }
    }

    override fun setAllTracksSortOrder(sortType: LibrarySortType): Completable {
        return Completable.fromCallable { preferences.edit { putInt(ALL_SONGS_SORT_ORDER, sortType.ordinal) } }
    }

    override fun setAllAlbumsSortOrder(sortType: LibrarySortType): Completable {
        return Completable.fromCallable { preferences.edit { putInt(ALL_ALBUMS_SORT_ORDER, sortType.ordinal) } }
    }

    override fun setAllArtistsSortOrder(sortType: LibrarySortType): Completable {
        return Completable.fromCallable { preferences.edit { putInt(ALL_ARTIST_SORT_ORDER, sortType.ordinal) } }
    }
}