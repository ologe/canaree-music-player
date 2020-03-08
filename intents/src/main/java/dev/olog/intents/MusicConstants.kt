package dev.olog.intents

object MusicConstants {

    private const val TAG = "MusicConstants"

    // used to adjust rpc delay
    const val STATE_EMISSION = "${TAG}_emission_time"

    const val PATH = "$TAG.PATH"
    const val IS_PODCAST = "$TAG.extra.is_podcast"
    const val SKIP_NEXT = "$TAG.extra.skip_next"
    const val SKIP_PREVIOUS = "$TAG.extra.skip_previous"

}