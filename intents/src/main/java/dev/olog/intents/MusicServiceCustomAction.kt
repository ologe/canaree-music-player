package dev.olog.intents

enum class MusicServiceCustomAction {
    SHUFFLE,

    SWAP,
    SWAP_RELATIVE, // position relative to current item

    REMOVE,
    REMOVE_RELATIVE, // position relative to current item

    MOVE_RELATIVE,

    FORWARD_10,
    FORWARD_30,
    REPLAY_10,
    REPLAY_30,

    ADD_TO_PLAY_LATER,
    ADD_TO_PLAY_NEXT,

    TOGGLE_FAVORITE;

    companion object {

        val ARGUMENT_FILTER = "${MusicServiceCustomAction::class.java}.filter"
        val ARGUMENT_SWAP_FROM = "${MusicServiceCustomAction::class.java}.swap.from"
        val ARGUMENT_SWAP_TO = "${MusicServiceCustomAction::class.java}.swap.to"
        val ARGUMENT_POSITION = "${MusicServiceCustomAction::class.java}.position"

        val ARGUMENT_MEDIA_ID_LIST = "${MusicServiceCustomAction::class.java}.mediaid.list"
        val ARGUMENT_IS_PODCAST = "${MusicServiceCustomAction::class.java}.podcast"
    }

}