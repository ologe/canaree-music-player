package dev.olog.intents

import android.provider.MediaStore
import kotlin.time.milliseconds

object AppConstants {

    private const val TAG = "AppConstants"
    const val ACTION_CONTENT_VIEW = "$TAG.action.content.view"

    val PROGRESS_BAR_INTERVAL = 50L.milliseconds

    const val UNKNOWN = MediaStore.UNKNOWN_STRING

}