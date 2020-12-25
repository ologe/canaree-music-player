package dev.olog.intents

enum class MusicServiceAction {
    PLAY,
    PLAY_URI,
    PLAY_PAUSE,

    SKIP_NEXT,
    SKIP_PREVIOUS;


    companion object {
        const val ARGUMENT_MEDIA_ID = "mediaid"
        const val ARGUMENT_SORT_TYPE = "sort.type"
        const val ARGUMENT_SORT_ARRANGING = "sort.arranging"

    }
}