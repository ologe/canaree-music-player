package dev.olog.data.recent.search

import dev.olog.core.MediaIdCategory

internal fun MediaIdCategory.recentSearchType(): Int = when (this) {
    MediaIdCategory.FOLDERS -> 0
    MediaIdCategory.PLAYLISTS -> 1
    MediaIdCategory.SONGS -> 2
    MediaIdCategory.ALBUMS -> 3
    MediaIdCategory.ARTISTS -> 4
    MediaIdCategory.GENRES -> 5
    MediaIdCategory.PODCASTS_PLAYLIST -> 6
    MediaIdCategory.PODCASTS -> 7
    MediaIdCategory.PODCASTS_ALBUMS -> 8
    MediaIdCategory.PODCASTS_ARTISTS -> 9
    MediaIdCategory.HEADER -> error(this)
    MediaIdCategory.PLAYING_QUEUE -> error(this)
}