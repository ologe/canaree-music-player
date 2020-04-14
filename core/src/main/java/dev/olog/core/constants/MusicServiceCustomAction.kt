package dev.olog.core.constants

enum class MusicServiceCustomAction {
    SHUFFLE,
    PLAY_RECENTLY_ADDED,
    PLAY_MOST_PLAYED,
    PLAY_SPOTIFY_PREVIEW,

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

        val ARGUMENT_MEDIA_ID = "${MusicServiceCustomAction::class.java}_mediaid"
        val ARGUMENT_FILTER = "${MusicServiceCustomAction::class.java}_filter"
        val ARGUMENT_SWAP_FROM = "${MusicServiceCustomAction::class.java}_swap_from"
        val ARGUMENT_SWAP_TO = "${MusicServiceCustomAction::class.java}_swap_to"
        val ARGUMENT_POSITION = "${MusicServiceCustomAction::class.java}_position"

        val ARGUMENT_MEDIA_ID_LIST = "${MusicServiceCustomAction::class.java}_mediaid_list"
    }

}