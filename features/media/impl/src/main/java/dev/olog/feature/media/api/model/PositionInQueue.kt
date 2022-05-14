package dev.olog.feature.media.api.model

internal enum class PositionInQueue {
    /**
     * Has only next songs
     */
    FIRST,
    /**
     * Has previous and next songs
     */
    IN_MIDDLE, // has previous and next songs
    /**
     * Has only previous songs
     */
    LAST,
    /**
     * Is the only song the list, has no previous or next songs
     */
    FIRST_AND_LAST
}
