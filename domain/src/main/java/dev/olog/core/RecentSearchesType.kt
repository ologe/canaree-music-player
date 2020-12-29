package dev.olog.core

enum class RecentSearchesType(val compatibilityValue: Int) {
    SONG(0),
    ARTIST(1),
    ALBUM(2),
    FOLDER(3),
    PLAYLIST(4),
    GENRE(5),

    PODCAST(6),
    PODCAST_PLAYLIST(7),
    PODCAST_ALBUM(8),
    PODCAST_ARTIST(9);

    val isPlayable: Boolean
        get() = this == SONG || this == PODCAST

}