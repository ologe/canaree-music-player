package dev.olog.feature.library

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import dev.olog.core.preference.Preference
import dev.olog.core.preference.PreferenceManager
import dev.olog.feature.library.tab.layout.manager.TabFragmentSpanSizeLookup
import javax.inject.Inject

internal class LibraryPrefsImpl @Inject constructor(
    private val preferenceManager: PreferenceManager,
    @ApplicationContext private val context: Context,
    private val preferences: SharedPreferences, // TODO remove
) : LibraryPrefs {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"

        private const val VIEW_PAGER_LAST_PAGE = "$TAG.VIEW_PAGER_LAST_PAGE"
        private const val VIEW_PAGER_PODCAST_LAST_PAGE = "$TAG.VIEW_PAGER_PODCAST_LAST_PAGE"

        private const val LIBRARY_LAST_PAGE = "$TAG.LIBRARY_PAGE"

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

        private const val CATEGORY_PODCAST_PLAYLIST_ORDER = "$TAG.CATEGORY_PODCAST_PLAYLIST_ORDER"
        private const val CATEGORY_PODCAST_ORDER = "$TAG.CATEGORY_PODCAST_ORDER"
        private const val CATEGORY_PODCAST_ALBUM_ORDER = "$TAG.CATEGORY_PODCAST_ALBUM_ORDER"
        private const val CATEGORY_PODCAST_ARTIST_ORDER = "$TAG.CATEGORY_PODCAST_ARTIST_ORDER"

        private const val CATEGORY_PODCAST_PLAYLIST_VISIBILITY =
            "$TAG.CATEGORY_PODCAST_PODCAST_PLAYLIST_VISIBILITY"
        private const val CATEGORY_PODCAST_VISIBILITY = "$TAG.CATEGORY_PODCAST_VISIBILITY"
        private const val CATEGORY_PODCAST_ALBUM_VISIBILITY =
            "$TAG.CATEGORY_PODCAST_ALBUM_VISIBILITY"
        private const val CATEGORY_PODCAST_ARTIST_VISIBILITY =
            "$TAG.CATEGORY_PODCAST_ARTIST_VISIBILITY"
    }

    override fun spanCount(
        category: MediaUri.Category,
        type: MediaStoreType,
    ): Preference<Int> {
        return preferenceManager.create("${category}_span_${type}", TabFragmentSpanSizeLookup.getDefaultSpanSize(context, category))
    }

    override val recentlyAddedVisibility: Preference<Boolean>
        get() = preferenceManager.create(prefs.R.string.prefs_show_new_albums_artists_key, true)

    override val recentlyPlayedVisibility: Preference<Boolean>
        get() = preferenceManager.create(prefs.R.string.prefs_show_recent_albums_artists_key, true)

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

    override fun getLastLibraryPage(): LibraryPage {
        val page = preferences.getString(LIBRARY_LAST_PAGE, LibraryPage.TRACKS.toString())!!
        return LibraryPage.valueOf(page)
    }

    override fun setLibraryPage(page: LibraryPage) {
        preferences.edit {
            putString(LIBRARY_LAST_PAGE, page.toString())
        }
    }

    override fun getLibraryCategories(): List<LibraryCategoryBehavior> {
        return listOf(
            LibraryCategoryBehavior(
                category = MediaUri.Category.Folder,
                visible = preferences.getBoolean(CATEGORY_FOLDER_VISIBILITY, true),
                order = preferences.getInt(CATEGORY_FOLDER_ORDER, 0),
                isPodcast = false,
            ),
            LibraryCategoryBehavior(
                category = MediaUri.Category.Playlist,
                visible = preferences.getBoolean(CATEGORY_PLAYLIST_VISIBILITY, true),
                order = preferences.getInt(CATEGORY_PLAYLIST_ORDER, 1),
                isPodcast = false,
            ),
            LibraryCategoryBehavior(
                category = MediaUri.Category.Track,
                visible = preferences.getBoolean(CATEGORY_SONG_VISIBILITY, true),
                order = preferences.getInt(CATEGORY_SONG_ORDER, 2),
                isPodcast = false,
            ),
            LibraryCategoryBehavior(
                category = MediaUri.Category.Collection,
                visible = preferences.getBoolean(CATEGORY_ALBUM_VISIBILITY, true),
                order = preferences.getInt(CATEGORY_ALBUM_ORDER, 3),
                isPodcast = false,
            ),
            LibraryCategoryBehavior(
                category = MediaUri.Category.Author,
                visible = preferences.getBoolean(CATEGORY_ARTIST_VISIBILITY, true),
                order = preferences.getInt(CATEGORY_ARTIST_ORDER, 4),
                isPodcast = false,
            ),
            LibraryCategoryBehavior(
                category = MediaUri.Category.Genre,
                visible = preferences.getBoolean(CATEGORY_GENRE_VISIBILITY, true),
                order = preferences.getInt(CATEGORY_GENRE_ORDER, 5),
                isPodcast = false,
            )
        ).sortedBy { it.order }
    }

    override fun getDefaultLibraryCategories(): List<LibraryCategoryBehavior> {
        return listOf(
            MediaUri.Category.Folder,
            MediaUri.Category.Playlist,
            MediaUri.Category.Track,
            MediaUri.Category.Collection,
            MediaUri.Category.Author,
            MediaUri.Category.Genre,
        ).mapIndexed { index, category ->
            LibraryCategoryBehavior(
                category = category,
                visible = true,
                order = index,
                isPodcast = false
            )
        }
    }

    override fun setLibraryCategories(behavior: List<LibraryCategoryBehavior>) {
        preferences.edit {
            val folder = behavior.first { it.category == MediaUri.Category.Folder }
            putInt(CATEGORY_FOLDER_ORDER, folder.order)
            putBoolean(CATEGORY_FOLDER_VISIBILITY, folder.visible)

            val playlist = behavior.first { it.category == MediaUri.Category.Playlist }
            putInt(CATEGORY_PLAYLIST_ORDER, playlist.order)
            putBoolean(CATEGORY_PLAYLIST_VISIBILITY, playlist.visible)

            val song = behavior.first { it.category == MediaUri.Category.Track }
            putInt(CATEGORY_SONG_ORDER, song.order)
            putBoolean(CATEGORY_SONG_VISIBILITY, song.visible)

            val album = behavior.first { it.category == MediaUri.Category.Collection }
            putInt(CATEGORY_ALBUM_ORDER, album.order)
            putBoolean(CATEGORY_ALBUM_VISIBILITY, album.visible)

            val artist = behavior.first { it.category == MediaUri.Category.Author }
            putInt(CATEGORY_ARTIST_ORDER, artist.order)
            putBoolean(CATEGORY_ARTIST_VISIBILITY, artist.visible)

            val genre = behavior.first { it.category == MediaUri.Category.Genre }
            putInt(CATEGORY_GENRE_ORDER, genre.order)
            putBoolean(CATEGORY_GENRE_VISIBILITY, genre.visible)
        }
    }

    override fun getPodcastLibraryCategories(): List<LibraryCategoryBehavior> {
        return listOf(
            LibraryCategoryBehavior(
                category = MediaUri.Category.Playlist,
                visible = preferences.getBoolean(CATEGORY_PODCAST_PLAYLIST_VISIBILITY, true),
                order = preferences.getInt(CATEGORY_PODCAST_PLAYLIST_ORDER, 0),
                isPodcast = true,
            ),
            LibraryCategoryBehavior(
                category = MediaUri.Category.Track,
                visible = preferences.getBoolean(CATEGORY_PODCAST_VISIBILITY, true),
                order = preferences.getInt(CATEGORY_PODCAST_ORDER, 1),
                isPodcast = true,
            ),
            LibraryCategoryBehavior(
                category = MediaUri.Category.Collection,
                visible = preferences.getBoolean(CATEGORY_PODCAST_ALBUM_VISIBILITY, true),
                order = preferences.getInt(CATEGORY_PODCAST_ALBUM_ORDER, 2),
                isPodcast = true,
            ),
            LibraryCategoryBehavior(
                MediaUri.Category.Author,
                preferences.getBoolean(CATEGORY_PODCAST_ARTIST_VISIBILITY, true),
                preferences.getInt(CATEGORY_PODCAST_ARTIST_ORDER, 3),
                isPodcast = true,
            )
        ).sortedBy { it.order }
    }

    override fun getDefaultPodcastLibraryCategories(): List<LibraryCategoryBehavior> {
        return listOf(
            MediaUri.Category.Playlist,
            MediaUri.Category.Track,
            MediaUri.Category.Collection,
            MediaUri.Category.Author,
        ).mapIndexed { index, category ->
            LibraryCategoryBehavior(
                category = category,
                visible = true,
                order = index,
                isPodcast = true
            )
        }
    }

    override fun setPodcastLibraryCategories(behavior: List<LibraryCategoryBehavior>) {
        preferences.edit {

            val playlist = behavior.first { it.category == MediaUri.Category.Playlist }
            putInt(CATEGORY_PODCAST_PLAYLIST_ORDER, playlist.order)
            putBoolean(CATEGORY_PODCAST_PLAYLIST_VISIBILITY, playlist.visible)

            val song = behavior.first { it.category == MediaUri.Category.Track }
            putInt(CATEGORY_PODCAST_ORDER, song.order)
            putBoolean(CATEGORY_PODCAST_VISIBILITY, song.visible)

            val album = behavior.first { it.category == MediaUri.Category.Collection }
            putInt(CATEGORY_PODCAST_ALBUM_ORDER, album.order)
            putBoolean(CATEGORY_PODCAST_ALBUM_VISIBILITY, album.visible)

            val artist = behavior.first { it.category == MediaUri.Category.Author }
            putInt(CATEGORY_PODCAST_ARTIST_ORDER, artist.order)
            putBoolean(CATEGORY_PODCAST_ARTIST_VISIBILITY, artist.visible)
        }
    }

    override fun canShowPodcasts(): Boolean {
        return preferences.getBoolean(context.getString(prefs.R.string.prefs_show_podcasts_key), true)
    }

    override val useFolderTree: Preference<Boolean>
        get() = preferenceManager.create(prefs.R.string.prefs_folder_tree_view_key, false)

}