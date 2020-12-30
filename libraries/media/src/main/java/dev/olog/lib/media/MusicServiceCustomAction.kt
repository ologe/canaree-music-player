package dev.olog.lib.media

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

        const val ARGUMENT_FILTER = "filter"
        const val ARGUMENT_SWAP_FROM = "swap.from"
        const val ARGUMENT_SWAP_TO = "swap.to"
        const val ARGUMENT_POSITION = "position"

        const val ARGUMENT_MEDIA_ID_LIST = "mediaid.list"
        const val ARGUMENT_IS_PODCAST = "podcast"
    }

}