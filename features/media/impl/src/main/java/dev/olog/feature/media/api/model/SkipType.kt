package dev.olog.feature.media.api.model

internal enum class SkipType {
    NONE,
    RESTART,
    SKIP_PREVIOUS,
    SKIP_NEXT,
    TRACK_ENDED
}