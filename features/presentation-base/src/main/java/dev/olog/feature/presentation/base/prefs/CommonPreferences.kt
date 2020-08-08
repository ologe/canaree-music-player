package dev.olog.feature.presentation.base.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.MediaIdCategory
import dev.olog.feature.presentation.base.R
import dev.olog.feature.presentation.base.model.LibraryCategoryBehavior
import dev.olog.navigation.screens.BottomNavigationPage
import dev.olog.navigation.screens.LibraryPage
import javax.inject.Inject

private const val TAG = "AppPreferencesDataStoreImpl"

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
private const val CATEGORY_PODCAST_ARTIST_ORDER = "$TAG.CATEGORY_PODCAST_ARTIST_ORDER"

private const val CATEGORY_PODCAST_PLAYLIST_VISIBILITY = "$TAG.CATEGORY_PODCAST_PODCAST_PLAYLIST_VISIBILITY"
private const val CATEGORY_PODCAST_VISIBILITY = "$TAG.CATEGORY_PODCAST_VISIBILITY"
private const val CATEGORY_PODCAST_ARTIST_VISIBILITY = "$TAG.CATEGORY_PODCAST_ARTIST_VISIBILITY"

private const val BOTTOM_VIEW_LAST_PAGE = "$TAG.BOTTOM_VIEW_3"

private const val LIBRARY_LAST_PAGE = "$TAG.LIBRARY_PAGE"

class CommonPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: SharedPreferences
) {

    fun getLibraryCategories(): List<LibraryCategoryBehavior> {
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

    fun setLibraryCategories(behavior: List<LibraryCategoryBehavior>) {
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

    fun getPodcastLibraryCategories(): List<LibraryCategoryBehavior> {
        return listOf(
            LibraryCategoryBehavior(
                MediaIdCategory.PODCASTS_PLAYLIST,
                preferences.getBoolean(CATEGORY_PODCAST_PLAYLIST_VISIBILITY, true),
                preferences.getInt(CATEGORY_PODCAST_PLAYLIST_ORDER, 0)
            ),
            LibraryCategoryBehavior(
                MediaIdCategory.PODCASTS,
                preferences.getBoolean(CATEGORY_PODCAST_VISIBILITY, true),
                preferences.getInt(CATEGORY_PODCAST_ORDER, 1)
            ),
            LibraryCategoryBehavior(
                MediaIdCategory.PODCASTS_AUTHORS,
                preferences.getBoolean(CATEGORY_PODCAST_ARTIST_VISIBILITY, true),
                preferences.getInt(CATEGORY_PODCAST_ARTIST_ORDER, 3)
            )
        ).sortedBy { it.order }
    }

    fun getDefaultPodcastLibraryCategories(): List<LibraryCategoryBehavior> {
        return MediaIdCategory.values()
            .drop(6)
            .take(4)
            .mapIndexed { index, category ->
                LibraryCategoryBehavior(
                    category,
                    true,
                    index
                )
            }
    }

    fun setPodcastLibraryCategories(behavior: List<LibraryCategoryBehavior>) {
        preferences.edit {

            val playlist = behavior.first { it.category == MediaIdCategory.PODCASTS_PLAYLIST }
            putInt(CATEGORY_PODCAST_PLAYLIST_ORDER, playlist.order)
            putBoolean(CATEGORY_PODCAST_PLAYLIST_VISIBILITY, playlist.visible)

            val song = behavior.first { it.category == MediaIdCategory.PODCASTS }
            putInt(CATEGORY_PODCAST_ORDER, song.order)
            putBoolean(CATEGORY_PODCAST_VISIBILITY, song.visible)

            val artist = behavior.first { it.category == MediaIdCategory.PODCASTS_AUTHORS }
            putInt(CATEGORY_PODCAST_ARTIST_ORDER, artist.order)
            putBoolean(CATEGORY_PODCAST_ARTIST_VISIBILITY, artist.visible)
        }
    }

    fun getDefaultLibraryCategories(): List<LibraryCategoryBehavior> {
        return MediaIdCategory.values()
            .take(6)
            .mapIndexed { index, category ->
                LibraryCategoryBehavior(
                    category,
                    true,
                    index
                )
            }
    }

    fun canShowPodcasts(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_show_podcasts_key), true)
    }

    fun getLastBottomViewPage(): BottomNavigationPage {
        val page = preferences.getString(BOTTOM_VIEW_LAST_PAGE, BottomNavigationPage.LIBRARY.toString())!!
        return BottomNavigationPage.valueOf(page)
    }

    fun setLastBottomViewPage(page: BottomNavigationPage) {
        preferences.edit { putString(BOTTOM_VIEW_LAST_PAGE, page.toString()) }
    }

    fun getLastLibraryPage(): LibraryPage {
        val page = preferences.getString(LIBRARY_LAST_PAGE, LibraryPage.TRACKS.toString())!!
        return LibraryPage.valueOf(page)
    }

    fun setLibraryPage(page: LibraryPage) {
        preferences.edit {
            putString(LIBRARY_LAST_PAGE, page.toString())
        }
    }

}