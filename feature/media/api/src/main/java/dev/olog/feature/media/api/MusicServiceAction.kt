package dev.olog.feature.media.api

enum class MusicServiceAction {
    PLAY,
    PLAY_URI,
    PLAY_PAUSE,

    SKIP_NEXT,
    SKIP_PREVIOUS;


    companion object {
        val ARGUMENT_MEDIA_ID = "${MusicServiceAction::class.java}.mediaid"
        val ARGUMENT_SORT_TYPE = "${MusicServiceAction::class.java}.sort.type"
        val ARGUMENT_SORT_ARRANGING = "${MusicServiceAction::class.java}.sort.arranging"

    }
}