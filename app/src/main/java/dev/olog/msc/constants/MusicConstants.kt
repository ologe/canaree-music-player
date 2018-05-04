package dev.olog.msc.constants

object MusicConstants {

    private const val TAG = "MusicConstants"
    const val ACTION_PLAY = "$TAG.shortcut.play"
    const val ACTION_SHUFFLE = "$TAG.action.play_shuffle"
    const val ACTION_PLAY_PAUSE = "$TAG.action.play"
    const val ACTION_SKIP_NEXT = "$TAG.action.skip.next"
    const val ACTION_SKIP_PREVIOUS = "$TAG.action.skip.previous"
    const val ACTION_TOGGLE_FAVORITE = "$TAG.action.toggle.favorite"

    const val ACTION_SWAP = TAG + "action.swap"
    const val ACTION_SWAP_RELATIVE = TAG + "action.swap_relative"
    const val ACTION_REMOVE = TAG + "action.remove"
    const val ACTION_REMOVE_RELATIVE = TAG + "action.remove_relative"

    const val ARGUMENT_SWAP_FROM = "$TAG.argument.swap_from"
    const val ARGUMENT_SWAP_TO = "$TAG.argument.swap_to"

    const val ARGUMENT_REMOVE_POSITION = "$TAG.argument.remove_position"

    const val BUNDLE_RECENTLY_PLAYED = "$TAG.bundle.recently.added"
    const val BUNDLE_MOST_PLAYED = "$TAG.bundle.most.played"

    const val ARGUMENT_SORT_TYPE = "$TAG.argument.sort.type"
    const val ARGUMENT_SORT_ARRANGING = "$TAG.argument.sort.arranging"

    const val IS_EXPLICIT = "$TAG.IS_EXPLICIT"
    const val IS_REMIX = "$TAG.IS_REMIX"
    const val PATH = "$TAG.PATH"

    const val EXTRA_QUEUE_CATEGORY = "$TAG.extra.queue_category"

}