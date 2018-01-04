package dev.olog.domain.entity

data class DetailSort(
        val sortType: SortType,
        val sortArranging: SortArranging
)

enum class SortType {
    TITLE, ARTIST, ALBUM, DURATION, RECENTLY_ADDED, TRACK_NUMBER, CUSTOM
}

enum class SortArranging {
    ASCENDING, DESCENDING
}