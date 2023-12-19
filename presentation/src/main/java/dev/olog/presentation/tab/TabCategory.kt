package dev.olog.presentation.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.R

@Stable
enum class TabCategory {
    FOLDERS,
    PLAYLISTS,
    SONGS,
    ALBUMS,
    ARTISTS,
    GENRES,

    PODCASTS_PLAYLIST,
    PODCASTS,
    PODCASTS_ARTISTS,
    PODCASTS_ALBUMS,
}

@Composable
fun TabCategory.asString(): String = when (this) {
    TabCategory.FOLDERS -> stringResource(R.string.category_folders)
    TabCategory.PLAYLISTS,
    TabCategory.PODCASTS_PLAYLIST -> stringResource(R.string.category_playlists)
    TabCategory.SONGS -> stringResource(R.string.category_songs)
    TabCategory.ALBUMS,
    TabCategory.PODCASTS_ALBUMS -> stringResource(R.string.category_albums)
    TabCategory.ARTISTS,
    TabCategory.PODCASTS_ARTISTS -> stringResource(R.string.category_artists)
    TabCategory.GENRES -> stringResource(R.string.category_genres)
    TabCategory.PODCASTS -> stringResource(R.string.category_podcasts)
}

@Composable
fun TabCategory.asEmptyStateString() = when (this) {
    TabCategory.FOLDERS -> stringArrayResource(R.array.tab_empty_state).getOrNull(0).orEmpty()
    TabCategory.PLAYLISTS -> stringArrayResource(R.array.tab_empty_state).getOrNull(1).orEmpty()
    TabCategory.SONGS -> stringArrayResource(R.array.tab_empty_state).getOrNull(2).orEmpty()
    TabCategory.ALBUMS -> stringArrayResource(R.array.tab_empty_state).getOrNull(3).orEmpty()
    TabCategory.ARTISTS -> stringArrayResource(R.array.tab_empty_state).getOrNull(4).orEmpty()
    TabCategory.GENRES -> stringArrayResource(R.array.tab_empty_state).getOrNull(5).orEmpty()
    TabCategory.PODCASTS_PLAYLIST -> stringArrayResource(R.array.tab_empty_podcast).getOrNull(0).orEmpty()
    TabCategory.PODCASTS -> stringArrayResource(R.array.tab_empty_podcast).getOrNull(1).orEmpty()
    TabCategory.PODCASTS_ALBUMS -> stringArrayResource(R.array.tab_empty_podcast).getOrNull(2).orEmpty()
    TabCategory.PODCASTS_ARTISTS -> stringArrayResource(R.array.tab_empty_podcast).getOrNull(3).orEmpty()
}

internal fun MediaIdCategory.toTabCategory(): TabCategory = when (this) {
    MediaIdCategory.FOLDERS -> TabCategory.FOLDERS
    MediaIdCategory.PLAYLISTS -> TabCategory.PLAYLISTS
    MediaIdCategory.SONGS -> TabCategory.SONGS
    MediaIdCategory.ALBUMS -> TabCategory.ALBUMS
    MediaIdCategory.ARTISTS -> TabCategory.ARTISTS
    MediaIdCategory.GENRES -> TabCategory.GENRES
    MediaIdCategory.PODCASTS_PLAYLIST -> TabCategory.PODCASTS_PLAYLIST
    MediaIdCategory.PODCASTS -> TabCategory.PODCASTS
    MediaIdCategory.PODCASTS_ALBUMS -> TabCategory.PODCASTS_ALBUMS
    MediaIdCategory.PODCASTS_ARTISTS -> TabCategory.PODCASTS_ARTISTS
    else -> throw IllegalArgumentException("invalid category $this")
}

internal fun TabCategory.toMediaIdCategory(): MediaIdCategory = when (this) {
    TabCategory.FOLDERS -> MediaIdCategory.FOLDERS
    TabCategory.PLAYLISTS -> MediaIdCategory.PLAYLISTS
    TabCategory.SONGS -> MediaIdCategory.SONGS
    TabCategory.ALBUMS -> MediaIdCategory.ALBUMS
    TabCategory.ARTISTS -> MediaIdCategory.ARTISTS
    TabCategory.GENRES -> MediaIdCategory.GENRES
    TabCategory.PODCASTS_PLAYLIST -> MediaIdCategory.PODCASTS_PLAYLIST
    TabCategory.PODCASTS -> MediaIdCategory.PODCASTS
    TabCategory.PODCASTS_ALBUMS -> MediaIdCategory.PODCASTS_ALBUMS
    TabCategory.PODCASTS_ARTISTS -> MediaIdCategory.PODCASTS_ARTISTS
}