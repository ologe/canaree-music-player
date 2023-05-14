package dev.olog.presentation.tab

import dev.olog.core.MediaIdCategory

internal enum class TabCategory {
    FOLDERS,
    PLAYLISTS,
    SONGS,
    ALBUMS,
    ARTISTS,
    GENRES,
}

internal fun MediaIdCategory.toTabCategory(): TabCategory = when (this) {
    MediaIdCategory.FOLDERS -> TabCategory.FOLDERS
    MediaIdCategory.PLAYLISTS -> TabCategory.PLAYLISTS
    MediaIdCategory.SONGS -> TabCategory.SONGS
    MediaIdCategory.ALBUMS -> TabCategory.ALBUMS
    MediaIdCategory.ARTISTS -> TabCategory.ARTISTS
    MediaIdCategory.GENRES -> TabCategory.GENRES
    else -> throw IllegalArgumentException("invalid category $this")
}