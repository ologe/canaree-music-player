package dev.olog.feature.library.tab.model

import dev.olog.core.MediaIdCategory

internal enum class TabFragmentCategory {
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

    LAST_PLAYED_ALBUMS,
    LAST_PLAYED_ARTISTS,
    LAST_PLAYED_PODCAST_ALBUMS,
    LAST_PLAYED_PODCAST_ARTISTS,

    RECENTLY_ADDED_ALBUMS,
    RECENTLY_ADDED_ARTISTS,
    RECENTLY_ADDED_PODCAST_ALBUMS,
    RECENTLY_ADDED_PODCAST_ARTISTS,
}

internal fun MediaIdCategory.toTabCategory(): TabFragmentCategory = when (this) {
    MediaIdCategory.FOLDERS -> TabFragmentCategory.FOLDERS
    MediaIdCategory.PLAYLISTS -> TabFragmentCategory.PLAYLISTS
    MediaIdCategory.SONGS -> TabFragmentCategory.SONGS
    MediaIdCategory.ALBUMS -> TabFragmentCategory.ALBUMS
    MediaIdCategory.ARTISTS -> TabFragmentCategory.ARTISTS
    MediaIdCategory.GENRES -> TabFragmentCategory.GENRES
    MediaIdCategory.PODCASTS_PLAYLIST -> TabFragmentCategory.PODCASTS_PLAYLIST
    MediaIdCategory.PODCASTS -> TabFragmentCategory.PODCASTS
    MediaIdCategory.PODCASTS_ALBUMS -> TabFragmentCategory.PODCASTS_ALBUMS
    MediaIdCategory.PODCASTS_ARTISTS -> TabFragmentCategory.PODCASTS_ARTISTS
    else -> throw IllegalArgumentException("invalid category $this")
}