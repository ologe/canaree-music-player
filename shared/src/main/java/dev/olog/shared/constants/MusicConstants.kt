package dev.olog.shared.constants

object MusicConstants {

    private const val TAG = "MusicConstants."
    const val ACTION_PLAY_SHUFFLE = TAG + "action.play_shuffle"
    const val ACTION_SWAP = TAG + "action.swap"
    const val ACTION_SWAP_RELATIVE = TAG + "action.swap_relative"

    const val ARGUMENT_SWAP_FROM = TAG + "argument.swap_from"
    const val ARGUMENT_SWAP_TO = TAG + "argument.swap_to"

    const val BUNDLE_RECENTLY_PLAYED = "RECENTLY_PLAYED"
    const val BUNDLE_MOST_PLAYED = "BUNDLE_MOST_PLAYED"
    const val ACTION_TOGGLE_FAVORITE = "ACTION_TOGGLE_FAVORITE"

}