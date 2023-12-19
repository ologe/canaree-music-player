package dev.olog.presentation.model

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.R
import dev.olog.presentation.library.LibraryPage
import dev.olog.presentation.tab.TabCategory
import dev.olog.shared.android.extensions.observeKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class PresentationPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: SharedPreferences
) : PresentationPreferencesGateway {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"

        private const val FIRST_ACCESS = "$TAG.FIRST_ACCESS"

        private const val VIEW_PAGER_LAST_PAGE = "$TAG.VIEW_PAGER_LAST_PAGE"
        private const val VIEW_PAGER_PODCAST_LAST_PAGE = "$TAG.VIEW_PAGER_PODCAST_LAST_PAGE"
        private const val BOTTOM_VIEW_LAST_PAGE = "$TAG.BOTTOM_VIEW_3"
        private const val LIBRARY_LAST_PAGE = "$TAG.LIBRARY_PAGE"

        const val CATEGORY_FOLDER_ORDER = "$TAG.CATEGORY_FOLDER_ORDER"
        const val CATEGORY_PLAYLIST_ORDER = "$TAG.CATEGORY_PLAYLIST_ORDER"
        const val CATEGORY_SONG_ORDER = "$TAG.CATEGORY_SONG_ORDER"
        const val CATEGORY_ALBUM_ORDER = "$TAG.CATEGORY_ALBUM_ORDER"
        const val CATEGORY_ARTIST_ORDER = "$TAG.CATEGORY_ARTIST_ORDER"
        const val CATEGORY_GENRE_ORDER = "$TAG.CATEGORY_GENRE_ORDER"

        const val CATEGORY_FOLDER_VISIBILITY = "$TAG.CATEGORY_FOLDER_VISIBILITY"
        const val CATEGORY_PLAYLIST_VISIBILITY = "$TAG.CATEGORY_PLAYLIST_VISIBILITY"
        const val CATEGORY_SONG_VISIBILITY = "$TAG.CATEGORY_SONG_VISIBILITY"
        const val CATEGORY_ALBUM_VISIBILITY = "$TAG.CATEGORY_ALBUM_VISIBILITY"
        const val CATEGORY_ARTIST_VISIBILITY = "$TAG.CATEGORY_ARTIST_VISIBILITY"
        const val CATEGORY_GENRE_VISIBILITY = "$TAG.CATEGORY_GENRE_VISIBILITY"

        const val CATEGORY_PODCAST_PLAYLIST_ORDER = "$TAG.CATEGORY_PODCAST_PLAYLIST_ORDER"
        const val CATEGORY_PODCAST_ORDER = "$TAG.CATEGORY_PODCAST_ORDER"
        const val CATEGORY_PODCAST_ALBUM_ORDER = "$TAG.CATEGORY_PODCAST_ALBUM_ORDER"
        const val CATEGORY_PODCAST_ARTIST_ORDER = "$TAG.CATEGORY_PODCAST_ARTIST_ORDER"

        const val CATEGORY_PODCAST_PLAYLIST_VISIBILITY = "$TAG.CATEGORY_PODCAST_PODCAST_PLAYLIST_VISIBILITY"
        const val CATEGORY_PODCAST_VISIBILITY = "$TAG.CATEGORY_PODCAST_VISIBILITY"
        const val CATEGORY_PODCAST_ALBUM_VISIBILITY = "$TAG.CATEGORY_PODCAST_ALBUM_VISIBILITY"
        const val CATEGORY_PODCAST_ARTIST_VISIBILITY = "$TAG.CATEGORY_PODCAST_ARTIST_VISIBILITY"
    }

    override fun isFirstAccess(): Boolean {
        val isFirstAccess = preferences.getBoolean(FIRST_ACCESS, true)

        if (isFirstAccess) {
            preferences.edit { putBoolean(FIRST_ACCESS, false) }
        }

        return isFirstAccess
    }

    override fun getViewPagerLibraryLastPage(): Int {
        return preferences.getInt(VIEW_PAGER_LAST_PAGE, 2)
    }

    override fun setViewPagerLibraryLastPage(lastPage: Int) {
        preferences.edit { putInt(VIEW_PAGER_LAST_PAGE, lastPage) }
    }

    override fun getViewPagerPodcastLastPage(): Int {
        return preferences.getInt(VIEW_PAGER_PODCAST_LAST_PAGE, 1)
    }

    override fun setViewPagerPodcastLastPage(lastPage: Int) {
        preferences.edit { putInt(VIEW_PAGER_PODCAST_LAST_PAGE, lastPage) }
    }

    override fun getLastBottomViewPage(): BottomNavigationPage {
        val page =
            preferences.getString(BOTTOM_VIEW_LAST_PAGE, BottomNavigationPage.LIBRARY.toString())!!
        return BottomNavigationPage.valueOf(page)
    }

    override fun setLastBottomViewPage(page: BottomNavigationPage) {
        preferences.edit { putString(BOTTOM_VIEW_LAST_PAGE, page.toString()) }
    }

    override fun getLastLibraryPage(): LibraryPage {
        val page = preferences.getString(LIBRARY_LAST_PAGE, LibraryPage.TRACKS.toString())!!
        return LibraryPage.valueOf(page)
    }

    override fun setLibraryPage(page: LibraryPage) {
        preferences.edit {
            putString(LIBRARY_LAST_PAGE, page.toString())
        }
    }

    override fun getLibraryCategories(): Flow<List<LibraryCategoryBehavior>> {
        return combine(LibraryCategoryPreference.TrackItems.map { it.observe(preferences) }) { list ->
            list.toList().sortedBy { it.order }
        }
    }

    override fun getDefaultLibraryCategories(): List<LibraryCategoryBehavior> {
        return LibraryCategoryPreference.TrackItems.map { it.getDefault() }
    }

    override fun setLibraryCategories(behavior: List<LibraryCategoryBehavior>) {
        for (item in behavior) {
            val preference = LibraryCategoryPreference.TrackItems.find { it.category == item.category }
            preference?.write(preferences, item.visible, item.order)
        }
    }

    override fun getPodcastLibraryCategories(): Flow<List<LibraryCategoryBehavior>> {
        return combine(LibraryCategoryPreference.PodcastItems.map { it.observe(preferences) }) { list ->
            list.toList().sortedBy { it.order }
        }
    }

    override fun getDefaultPodcastLibraryCategories(): List<LibraryCategoryBehavior> {
        return LibraryCategoryPreference.PodcastItems.map { it.getDefault() }
    }

    override fun setPodcastLibraryCategories(behavior: List<LibraryCategoryBehavior>) {
        for (item in behavior) {
            val preference = LibraryCategoryPreference.PodcastItems.find { it.category == item.category }
            preference?.write(preferences, item.visible, item.order)
        }
    }

    override fun setDefault() {
        setLibraryCategories(getDefaultLibraryCategories())
        setPodcastLibraryCategories(getDefaultPodcastLibraryCategories())
    }

    override fun observeLibraryNewVisibility(): Flow<Boolean> {
        return preferences.observeKey(
            context.getString(R.string.prefs_show_new_albums_artists_key),
            true
        )
    }

    override fun observeLibraryRecentPlayedVisibility(): Flow<Boolean> {
        return preferences.observeKey(
            (context.getString(R.string.prefs_show_recent_albums_artists_key)),
            true
        )
    }

    override fun observePlayerControlsVisibility(): Flow<Boolean> {
        return preferences.observeKey(context.getString(R.string.prefs_player_controls_visibility_key), false)
    }

    override fun isAdaptiveColorEnabled(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_adaptive_colors_key), false)
    }

    override fun getSpanCount(category: TabCategory): Int {
        return preferences.getInt("${category}_span", SpanCountController.getDefaultSpan(context, category))
    }

    override fun observeSpanCount(category: TabCategory): Flow<Int> {
        return preferences.observeKey("${category}_span", SpanCountController.getDefaultSpan(context, category))
    }

    override fun setSpanCount(category: TabCategory, spanCount: Int) {
        preferences.edit {
            putInt("${category}_span", spanCount)
        }
    }

    override fun canShowPodcasts(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_show_podcasts_key), true)
    }

    override fun showFolderAsHierarchy(): Flow<Boolean> {
        return preferences.observeKey(
            context.getString(R.string.prefs_folder_tree_view_key),
            false,
        )
    }
}

enum class LibraryCategoryPreference(
    val category: MediaIdCategory,
    val visibleKey: String,
    val orderKey: String,
    val defaultOrder: Int
) {
    Folders(
        MediaIdCategory.FOLDERS,
        PresentationPreferencesImpl.CATEGORY_FOLDER_VISIBILITY,
        PresentationPreferencesImpl.CATEGORY_FOLDER_ORDER,
        0
    ),
    Playlists(
        MediaIdCategory.PLAYLISTS,
        PresentationPreferencesImpl.CATEGORY_PLAYLIST_VISIBILITY,
        PresentationPreferencesImpl.CATEGORY_PLAYLIST_ORDER,
        1,
    ),
    Songs(
        MediaIdCategory.SONGS,
        PresentationPreferencesImpl.CATEGORY_SONG_VISIBILITY,
        PresentationPreferencesImpl.CATEGORY_SONG_ORDER,
        2
    ),
    Albums(
        MediaIdCategory.ALBUMS,
        PresentationPreferencesImpl.CATEGORY_ALBUM_VISIBILITY,
        PresentationPreferencesImpl.CATEGORY_ALBUM_ORDER,
        3
    ),
    Artists(
        MediaIdCategory.ARTISTS,
        PresentationPreferencesImpl.CATEGORY_ARTIST_VISIBILITY,
        PresentationPreferencesImpl.CATEGORY_ARTIST_ORDER,
        4
    ),
    Genres(
        MediaIdCategory.GENRES,
        PresentationPreferencesImpl.CATEGORY_GENRE_VISIBILITY,
        PresentationPreferencesImpl.CATEGORY_GENRE_ORDER,
        5
    ),
    PodcastPlaylists(
        MediaIdCategory.PODCASTS_PLAYLIST,
        PresentationPreferencesImpl.CATEGORY_PODCAST_PLAYLIST_VISIBILITY,
        PresentationPreferencesImpl.CATEGORY_PODCAST_PLAYLIST_ORDER,
        0,
    ),
    Podcasts(
        MediaIdCategory.PODCASTS,
        PresentationPreferencesImpl.CATEGORY_PODCAST_VISIBILITY,
        PresentationPreferencesImpl.CATEGORY_PODCAST_ORDER,
        1,
    ),
    PodcastAlbums(
        MediaIdCategory.PODCASTS_ALBUMS,
        PresentationPreferencesImpl.CATEGORY_PODCAST_ALBUM_VISIBILITY,
        PresentationPreferencesImpl.CATEGORY_PODCAST_ALBUM_ORDER,
        2
    ),
    PodcastArtists(
        MediaIdCategory.PODCASTS_ARTISTS,
        PresentationPreferencesImpl.CATEGORY_PODCAST_ARTIST_VISIBILITY,
        PresentationPreferencesImpl.CATEGORY_PODCAST_ARTIST_ORDER,
        3
    );

    companion object {
        val TrackItems = listOf(
            Folders,
            Playlists,
            Songs,
            Albums,
            Artists,
            Genres,
        )

        val PodcastItems = listOf(
            PodcastPlaylists,
            Podcasts,
            PodcastAlbums,
            PodcastArtists,
        )
    }

    fun observe(preferences: SharedPreferences): Flow<LibraryCategoryBehavior> {
        return combine(
            preferences.observeKey(visibleKey, true),
            preferences.observeKey(orderKey, defaultOrder),
        ) { visible, order ->
            LibraryCategoryBehavior(category, visible, order)
        }
    }

    fun getDefault(): LibraryCategoryBehavior {
        return LibraryCategoryBehavior(category, true, defaultOrder)
    }

    fun write(
        preferences: SharedPreferences,
        isVisible: Boolean,
        order: Int,
    ) {
        preferences.edit {
            putBoolean(visibleKey, isVisible)
            putInt(orderKey, order)
        }
    }

}