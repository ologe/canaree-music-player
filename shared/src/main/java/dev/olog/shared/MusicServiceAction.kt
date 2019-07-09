package dev.olog.shared

enum class MusicServiceAction {
    PLAY,
    PLAY_URI,
    PLAY_PAUSE,
    PLAY_RECENTLY_ADDED,
    PLAY_MOST_PLAYED,
    SHUFFLE,

    SKIP_NEXT,
    SKIP_PREVIOUS,

    FORWARD_10,
    FORWARD_30,
    REPLAY_10,
    REPLAY_30,

    TOGGLE_FAVORITE,

    SWAP,
    SWAP_RELATIVE, // position relative to current item

    REMOVE,
    REMOVE_RELATIVE; // position relative to current item


    companion object {
        val ARGUMENT_MEDIA_ID = "${MusicServiceAction::class.java}.mediaid"
        val ARGUMENT_ID = "${MusicServiceAction::class.java}.id"
        val ARGUMENT_SWAP_FROM = "${MusicServiceAction::class.java}.swap.from"
        val ARGUMENT_SWAP_TO = "${MusicServiceAction::class.java}.swap.to"
        val ARGUMENT_POSITION = "${MusicServiceAction::class.java}.position"

        val ARGUMENT_SORT_TYPE = "${MusicServiceAction::class.java}.sort.type"
        val ARGUMENT_SORT_ARRANGING = "${MusicServiceAction::class.java}.sort.arranging"

        fun valueOfOrNull(value: String): MusicServiceAction? {
            return try {
                valueOf(value)
            } catch (ex: Exception) {
                return null
            }
        }

    }
}