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
    PODCASTS_ALBUMS,
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