package dev.olog.core.mediaid

enum class MediaIdModifier {
    MOST_PLAYED,
    RECENTLY_ADDED,
    SHUFFLE;

    companion object {

        fun find(value: String): MediaIdModifier? {
            return values().find { it.name == value }
        }

    }

}