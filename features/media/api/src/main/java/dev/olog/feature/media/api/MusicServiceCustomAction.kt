package dev.olog.feature.media.api

enum class MusicServiceCustomAction {
    SHUFFLE,
    PLAY_RECENTLY_ADDED,
    PLAY_MOST_PLAYED,

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

        const val ARGUMENT_MEDIA_ID = "custom.action.mediaid"
        const val ARGUMENT_FILTER = "custom.action.filter"
        const val ARGUMENT_SWAP_FROM = "custom.action.swap.from"
        const val ARGUMENT_SWAP_TO = "custom.action.swap.to"
        const val ARGUMENT_POSITION = "custom.action.position"

        const val ARGUMENT_MEDIA_ID_LIST = "custom.action.mediaid.list"
        const val ARGUMENT_IS_PODCAST = "custom.action.podcast"
    }

}