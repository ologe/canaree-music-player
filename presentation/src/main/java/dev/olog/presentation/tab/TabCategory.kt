package dev.olog.presentation.tab

import dev.olog.core.MediaIdCategory

internal enum class TabCategory {
    FOLDERS,
    PLAYLISTS,
    SONGS,
    ALBUMS,
    ARTISTS,
    GENRES,

    PODCASTS_PLAYLIST,
    PODCASTS,
    PODCASTS_ARTISTS,

    LAST_PLAYED_ALBUMS,
    LAST_PLAYED_ARTISTS,
    LAST_PLAYED_PODCAST_ARTISTS,

    RECENTLY_ADDED_ALBUMS,
    RECENTLY_ADDED_ARTISTS,
    RECENTLY_ADDED_PODCAST_ARTISTS,
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
    MediaIdCategory.PODCASTS_ARTISTS -> TabCategory.PODCASTS_ARTISTS
    else -> throw IllegalArgumentException("invalid category $this")
}