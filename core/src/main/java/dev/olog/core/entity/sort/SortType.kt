package dev.olog.core.entity.sort

import dev.olog.core.Migration

@Migration
enum class SortType {
    TITLE, ARTIST, ALBUM_ARTIST, ALBUM, DURATION, RECENTLY_ADDED, TRACK_NUMBER, CUSTOM
}

