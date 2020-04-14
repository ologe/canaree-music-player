package dev.olog.core.constants

enum class MusicServiceAction {
    PLAY,
    PLAY_URI,
    PLAY_PAUSE,

    SKIP_NEXT,
    SKIP_PREVIOUS;


    companion object {
        @JvmStatic
        val ARGUMENT_MEDIA_ID = "${MusicServiceAction::class.java}.mediaid"
        @JvmStatic
        val ARGUMENT_SORT_TYPE = "${MusicServiceAction::class.java}.sort.type"
        @JvmStatic
        val ARGUMENT_SORT_ARRANGING = "${MusicServiceAction::class.java}.sort.arranging"

    }
}