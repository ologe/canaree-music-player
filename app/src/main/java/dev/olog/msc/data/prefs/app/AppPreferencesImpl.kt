package dev.olog.msc.data.prefs.app

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.core.content.edit
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.GridSpanSize
import dev.olog.msc.domain.entity.LibraryCategoryBehavior
import dev.olog.msc.domain.entity.UserCredentials
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.gateway.prefs.Sorting
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.configuration
import dev.olog.msc.utils.k.extension.isOneHanded
import dev.olog.msc.utils.k.extension.isPortrait
import dev.olog.msc.utils.k.extension.safeGetCanonicalPath
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import java.io.File
import javax.inject.Inject

class AppPreferencesImpl @Inject constructor(
        @ApplicationContext private val context: Context,
        private val preferences: SharedPreferences,
        private val rxPreferences: RxSharedPreferences

) : AppPreferencesGateway,
        Sorting by AppSortingImpl(preferences, rxPreferences) {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"
        private const val FIRST_ACCESS = "$TAG.FIRST_ACCESS"

        private const val VIEW_PAGER_LAST_PAGE = "$TAG.VIEW_PAGER_LAST_PAGE"

        private const val SLEEP_TIME = "$TAG.SLEEP_TIME"
        private const val SLEEP_FROM = "$TAG.FROM_WHEN"


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

        private const val CATEGORY_FOLDER_SPAN_COUNT_ONE_HANDED = "$TAG.CATEGORY_FOLDER_SPAN_COUNT_ONE_HANDED"
        private const val CATEGORY_FOLDER_SPAN_COUNT_TWO_HANDED = "$TAG.CATEGORY_FOLDER_SPAN_COUNT_TWO_HANDED"
        private const val CATEGORY_PLAYLIST_SPAN_COUNT_ONE_HANDED = "$TAG.CATEGORY_PLAYLIST_SPAN_COUNT_ONE_HANDED"
        private const val CATEGORY_PLAYLIST_SPAN_COUNT_TWO_HANDED = "$TAG.CATEGORY_PLAYLIST_SPAN_COUNT_TWO_HANDED"
        private const val CATEGORY_ALBUM_SPAN_COUNT_ONE_HANDED = "$TAG.CATEGORY_ALBUM_SPAN_COUNT_ONE_HANDED"
        private const val CATEGORY_ALBUM_SPAN_COUNT_TWO_HANDED = "$TAG.CATEGORY_ALBUM_SPAN_COUNT_TWO_HANDED"
        private const val CATEGORY_ARTIST_SPAN_COUNT_ONE_HANDED = "$TAG.CATEGORY_ARTIST_SPAN_COUNT_ONE_HANDED"
        private const val CATEGORY_ARTIST_SPAN_COUNT_TWO_HANDED = "$TAG.CATEGORY_ARTIST_SPAN_COUNT_TWO_HANDED"
        private const val CATEGORY_GENRE_SPAN_COUNT_ONE_HANDED = "$TAG.CATEGORY_GENRE_SPAN_COUNT_ONE_HANDED"
        private const val CATEGORY_GENRE_SPAN_COUNT_TWO_HANDED = "$TAG.CATEGORY_GENRE_SPAN_COUNT_TWO_HANDED"

        private const val LAST_FM_USERNAME = "$TAG.LAST_FM_USERNAME"
        private const val LAST_FM_PASSWORD = "$TAG.LAST_FM_PASSWORD"

        private const val SYNC_ADJUSTMENT = "$TAG.SYNC_ADJUSTMENT"

        private const val BLACKLIST = "$TAG.BLACKLIST"

        private const val DEFAULT_MUSIC_FOLDER = "$TAG.DEFAULT_MUSIC_FOLDER"
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
            setLastFmCredentials(UserCredentials("", ""))

            emitter.onComplete()
        }
    }

    override fun observeAutoCreateImages(): Observable<Boolean> {
        return rxPreferences.getBoolean(context.getString(R.string.prefs_auto_create_images_key), true)
                .asObservable()
    }

    private fun setDefaultAutoDownloadImages(){
        preferences.edit {
            putString(context.getString(R.string.prefs_auto_download_images_key), context.getString(R.string.prefs_auto_download_images_entry_value_wifi))
            putBoolean(context.getString(R.string.prefs_auto_create_images_key), true)
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
            putString(context.getString(R.string.prefs_dark_mode_key), context.getString(R.string.prefs_dark_mode_entry_value_white))
        }
    }

    /*
            Must be encrypted
         */
    override fun getLastFmCredentials(): UserCredentials {
        return UserCredentials(
                preferences.getString(LAST_FM_USERNAME, ""),
                preferences.getString(LAST_FM_PASSWORD, "")
        )
    }

    /*
        Must be encrypted
     */
    override fun observeLastFmCredentials(): Observable<UserCredentials> {
        return rxPreferences.getString(LAST_FM_USERNAME, "")
                .asObservable()
                .map { UserCredentials(
                        it,
                        preferences.getString(LAST_FM_PASSWORD, "")
                ) }
    }

    /*
        Must be encrypted
     */
    override fun setLastFmCredentials(user: UserCredentials) {
        preferences.edit {
            putString(LAST_FM_USERNAME, user.username)
            putString(LAST_FM_PASSWORD, user.password)
        }
    }

    override fun getSyncAdjustment(): Long {
        return preferences.getLong(SYNC_ADJUSTMENT, 0)
    }

    override fun setSyncAdjustment(value: Long) {
        preferences.edit { putLong(SYNC_ADJUSTMENT, value) }
    }

    override fun observeAlbumSpanSize(): Observable<GridSpanSize> {
        return Observables.combineLatest(
                rxPreferences.getInteger(CATEGORY_ALBUM_SPAN_COUNT_ONE_HANDED, 2).asObservable(),
                rxPreferences.getInteger(CATEGORY_ALBUM_SPAN_COUNT_TWO_HANDED, 4).asObservable(),
                { one, two -> GridSpanSize(one, two) }
        )
    }

    override fun setAlbumSpanSize(spanSize: Int) {
        if (context.isOneHanded()){
            preferences.edit { putInt(CATEGORY_ALBUM_SPAN_COUNT_ONE_HANDED, spanSize) }
        } else {
            preferences.edit { putInt(CATEGORY_ALBUM_SPAN_COUNT_TWO_HANDED, spanSize) }
        }
    }

    override fun getDefaultMusicFolder(): File {
        val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        var startFolder = File(File.separator)
        if (musicDir.exists() && musicDir.isDirectory){
            startFolder = musicDir
        } else {
            val externalStorage = Environment.getExternalStorageDirectory()
            if (externalStorage.exists() && externalStorage.isDirectory){
                startFolder = externalStorage
            }
        }

        return File(preferences.getString(DEFAULT_MUSIC_FOLDER, startFolder.path))
    }

    override fun setDefaultMusicFolder(file: File) {
        preferences.edit {
            putString(DEFAULT_MUSIC_FOLDER, file.safeGetCanonicalPath())
        }
    }
}