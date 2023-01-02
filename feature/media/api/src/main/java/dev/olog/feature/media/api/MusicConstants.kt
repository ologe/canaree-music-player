package dev.olog.feature.media.api

import android.provider.MediaStore

object MusicConstants {

    private const val TAG = "MusicConstants"

    const val PATH = "$TAG.PATH"
    const val IS_PODCAST = "$TAG.extra.is_podcast"
    const val SKIP_NEXT = "$TAG.extra.skip_next"
    const val SKIP_PREVIOUS = "$TAG.extra.skip_previous"

    const val PROGRESS_BAR_INTERVAL = 50L
    const val UNKNOWN = MediaStore.UNKNOWN_STRING

}