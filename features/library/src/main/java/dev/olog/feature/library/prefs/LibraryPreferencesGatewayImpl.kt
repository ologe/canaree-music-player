package dev.olog.feature.library.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.feature.library.R
import dev.olog.feature.library.library.LibraryFragmentCategoryState
import dev.olog.feature.library.tab.model.TabFragmentCategory
import dev.olog.feature.library.tab.span.TabFragmentSpanCountController
import dev.olog.navigation.BottomNavigationPage
import dev.olog.shared.android.extensions.observeKey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class LibraryPreferencesGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: SharedPreferences
) : LibraryPreferencesGateway {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"

        private const val LIBRARY_TRACKS_PAGE = "$TAG.VIEW_PAGER_LAST_PAGE"
        private const val LIBRARY_PODCAST_PAGE = "$TAG.VIEW_PAGER_PODCAST_LAST_PAGE"
        private const val BOTTOM_NAVIGATION_PAGE = "$TAG.BOTTOM_VIEW_4"

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

    override var bottomNavigationPage: BottomNavigationPage
        get() {
            val page = preferences.getString(BOTTOM_NAVIGATION_PAGE, BottomNavigationPage.LIBRARY_TRACKS.toString())!!
            return BottomNavigationPage.valueOf(page)
        }
        set(value) {
            preferences.edit {
                putString(BOTTOM_NAVIGATION_PAGE, value.toString())
            }
        }

    override var libraryTracksLastPage: Int
        get() = preferences.getInt(LIBRARY_TRACKS_PAGE, 2)
        set(value) {
            preferences.edit {
                putInt(LIBRARY_TRACKS_PAGE, value)
            }
        }

    override var libraryPodcastsLastPage: Int
        get() = preferences.getInt(LIBRARY_PODCAST_PAGE, 1)
        set(value) {
            preferences.edit {
                putInt(LIBRARY_PODCAST_PAGE, value)
            }
        }

    override fun getLibraryCategories(): List<LibraryFragmentCategoryState> {
        return listOf(
            LibraryFragmentCategoryState(
                MediaIdCategory.FOLDERS,
                preferences.getBoolean(CATEGORY_FOLDER_VISIBILITY, true),
                preferences.getInt(CATEGORY_FOLDER_ORDER, 0)
            ),
            LibraryFragmentCategoryState(
                MediaIdCategory.PLAYLISTS,
                preferences.getBoolean(CATEGORY_PLAYLIST_VISIBILITY, true),
                preferences.getInt(CATEGORY_PLAYLIST_ORDER, 1)
            ),
            LibraryFragmentCategoryState(
                MediaIdCategory.SONGS,
                preferences.getBoolean(CATEGORY_SONG_VISIBILITY, true),
                preferences.getInt(CATEGORY_SONG_ORDER, 2)
            ),
            LibraryFragmentCategoryState(
                MediaIdCategory.ALBUMS,
                preferences.getBoolean(CATEGORY_ALBUM_VISIBILITY, true),
                preferences.getInt(CATEGORY_ALBUM_ORDER, 3)
            ),
            LibraryFragmentCategoryState(
                MediaIdCategory.ARTISTS,
                preferences.getBoolean(CATEGORY_ARTIST_VISIBILITY, true),
                preferences.getInt(CATEGORY_ARTIST_ORDER, 4)
            ),
            LibraryFragmentCategoryState(
                MediaIdCategory.GENRES,
                preferences.getBoolean(CATEGORY_GENRE_VISIBILITY, true),
                preferences.getInt(CATEGORY_GENRE_ORDER, 5)
            )
        ).sortedBy { it.order }
    }

    override fun getDefaultLibraryCategories(): List<LibraryFragmentCategoryState> {
        return MediaIdCategory.values()
            .take(6)
            .mapIndexed { index, category -> LibraryFragmentCategoryState(category, true, index) }
    }

    override fun setLibraryCategories(behavior: List<LibraryFragmentCategoryState>) {
        preferences.edit {
            val folder = behavior.first { it.category == MediaIdCategory.FOLDERS }
            putInt(CATEGORY_FOLDER_ORDER, folder.order)
            putBoolean(CATEGORY_FOLDER_VISIBILITY, folder.visible)

            val playlist = behavior.first { it.category == MediaIdCategory.PLAYLISTS }
            putInt(CATEGORY_PLAYLIST_ORDER, playlist.order)
            putBoolean(CATEGORY_PLAYLIST_VISIBILITY, playlist.visible)

            val track = behavior.first { it.category == MediaIdCategory.SONGS }
            putInt(CATEGORY_SONG_ORDER, track.order)
            putBoolean(CATEGORY_SONG_VISIBILITY, track.visible)

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

    override fun getPodcastLibraryCategories(): List<LibraryFragmentCategoryState> {
        return listOf(
            LibraryFragmentCategoryState(
                MediaIdCategory.PODCASTS_PLAYLIST,
                preferences.getBoolean(CATEGORY_PODCAST_PLAYLIST_VISIBILITY, true),
                preferences.getInt(CATEGORY_PODCAST_PLAYLIST_ORDER, 0)
            ),
            LibraryFragmentCategoryState(
                MediaIdCategory.PODCASTS,
                preferences.getBoolean(CATEGORY_PODCAST_VISIBILITY, true),
                preferences.getInt(CATEGORY_PODCAST_ORDER, 1)
            ),
            LibraryFragmentCategoryState(
                MediaIdCategory.PODCASTS_ALBUMS,
                preferences.getBoolean(CATEGORY_PODCAST_ALBUM_VISIBILITY, true),
                preferences.getInt(CATEGORY_PODCAST_ALBUM_ORDER, 2)
            ),
            LibraryFragmentCategoryState(
                MediaIdCategory.PODCASTS_ARTISTS,
                preferences.getBoolean(CATEGORY_PODCAST_ARTIST_VISIBILITY, true),
                preferences.getInt(CATEGORY_PODCAST_ARTIST_ORDER, 3)
            )
        ).sortedBy { it.order }
    }

    override fun getDefaultPodcastLibraryCategories(): List<LibraryFragmentCategoryState> {
        return MediaIdCategory.values()
            .drop(6)
            .take(4)
            .mapIndexed { index, category -> LibraryFragmentCategoryState(category, true, index) }
    }

    override fun setPodcastLibraryCategories(behavior: List<LibraryFragmentCategoryState>) {
        preferences.edit {

            val playlist = behavior.first { it.category == MediaIdCategory.PODCASTS_PLAYLIST }
            putInt(CATEGORY_PODCAST_PLAYLIST_ORDER, playlist.order)
            putBoolean(CATEGORY_PODCAST_PLAYLIST_VISIBILITY, playlist.visible)

            val track = behavior.first { it.category == MediaIdCategory.PODCASTS }
            putInt(CATEGORY_PODCAST_ORDER, track.order)
            putBoolean(CATEGORY_PODCAST_VISIBILITY, track.visible)

            val album = behavior.first { it.category == MediaIdCategory.PODCASTS_ALBUMS }
            putInt(CATEGORY_PODCAST_ALBUM_ORDER, album.order)
            putBoolean(CATEGORY_PODCAST_ALBUM_VISIBILITY, album.visible)

            val artist = behavior.first { it.category == MediaIdCategory.PODCASTS_ARTISTS }
            putInt(CATEGORY_PODCAST_ARTIST_ORDER, artist.order)
            putBoolean(CATEGORY_PODCAST_ARTIST_VISIBILITY, artist.visible)
        }
    }

    // TODO reset all??
    override fun reset() {
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

    override fun getSpanCount(category: TabFragmentCategory): Int {
        return preferences.getInt("${category}_span", TabFragmentSpanCountController.getDefaultSpan(context, category))
    }

    override fun observeSpanCount(category: TabFragmentCategory): Flow<Int> {
        return preferences.observeKey("${category}_span", TabFragmentSpanCountController.getDefaultSpan(context, category))
    }

    override fun setSpanCount(category: TabFragmentCategory, spanCount: Int) {
        preferences.edit {
            putInt("${category}_span", spanCount)
        }
    }

}