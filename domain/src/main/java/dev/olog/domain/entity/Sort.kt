package dev.olog.domain.entity

data class Sort(
    val type: Type,
    val arranging: Arranging
) {

    enum class Arranging {
        ASCENDING, DESCENDING;

        override fun toString(): String {
            if (this == ASCENDING){
                return "ASC"
            }
            return "DESC"
        }

    }

    enum class Type {
        TITLE,
        ARTIST,
        ALBUM_ARTIST,
        ALBUM,
        DURATION,
        RECENTLY_ADDED,
        TRACK_NUMBER,
        CUSTOM
    }

}