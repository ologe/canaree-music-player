package dev.olog.msc.constants

object MusicConstants {

    private const val TAG = "MusicConstants."
    const val ACTION_PLAY_SHUFFLE = TAG + "action.play_shuffle"
    const val SKIP_TO_ITEM = TAG + "action.skip_to_item"
    const val ACTION_SWAP = TAG + "action.swap"
    const val ACTION_SWAP_RELATIVE = TAG + "action.swap_relative"
    const val ACTION_REMOVE = TAG + "action.remove"
    const val ACTION_REMOVE_RELATIVE = TAG + "action.remove_relative"

    const val ARGUMENT_SWAP_FROM = TAG + "argument.swap_from"
    const val ARGUMENT_SWAP_TO = TAG + "argument.swap_to"

    const val ARGUMENT_REMOVE_POSITION = TAG + "argument.remove_position"

    const val BUNDLE_RECENTLY_PLAYED = TAG + "RECENTLY_PLAYED"
    const val BUNDLE_MOST_PLAYED = TAG + "BUNDLE_MOST_PLAYED"

    const val ARGUMENT_SORT_TYPE = TAG + "ARGUMENT_SORT_TYPE"
    const val ARGUMENT_SORT_ARRANGING = TAG + "ARGUMENT_SORT_ARRANGING"

}