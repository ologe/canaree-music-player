package dev.olog.presentation.tab

import dev.olog.presentation.PresentationIdCategory

internal enum class TabCategory {
    FOLDERS,
    PLAYLISTS,
    SONGS,
    ALBUMS,
    ARTISTS,
    GENRES,

    PODCASTS_PLAYLIST,
    PODCASTS,
    PODCASTS_AUTHORS,

    LAST_PLAYED_ALBUMS,
    LAST_PLAYED_ARTISTS,
    LAST_PLAYED_PODCAST_ARTISTS,

    RECENTLY_ADDED_ALBUMS,
    RECENTLY_ADDED_ARTISTS,
    RECENTLY_ADDED_PODCAST_ARTISTS,
}

internal fun PresentationIdCategory.toTabCategory(): TabCategory = when (this) {
    PresentationIdCategory.FOLDERS -> TabCategory.FOLDERS
    PresentationIdCategory.PLAYLISTS -> TabCategory.PLAYLISTS
    PresentationIdCategory.SONGS -> TabCategory.SONGS
    PresentationIdCategory.ALBUMS -> TabCategory.ALBUMS
    PresentationIdCategory.ARTISTS -> TabCategory.ARTISTS
    PresentationIdCategory.GENRES -> TabCategory.GENRES
    PresentationIdCategory.PODCASTS_PLAYLIST -> TabCategory.PODCASTS_PLAYLIST
    PresentationIdCategory.PODCASTS -> TabCategory.PODCASTS
    PresentationIdCategory.PODCASTS_AUTHORS -> TabCategory.PODCASTS_AUTHORS
    else -> throw IllegalArgumentException("invalid category $this")
}