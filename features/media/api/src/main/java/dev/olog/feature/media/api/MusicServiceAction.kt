package dev.olog.feature.media.api

enum class MusicServiceAction {
    PLAY,
    PLAY_URI,
    PLAY_PAUSE,

    SKIP_NEXT,
    SKIP_PREVIOUS;


    companion object {
        const val ARGUMENT_MEDIA_ID = "action.mediaid"
        const val ARGUMENT_SORT_TYPE = "action.sort.type"
        const val ARGUMENT_SORT_ARRANGING = "action.sort.arranging"

    }
}