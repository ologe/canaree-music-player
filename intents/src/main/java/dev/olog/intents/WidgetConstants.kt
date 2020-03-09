package dev.olog.intents

object WidgetConstants {

    private const val TAG = "WidgetConstants"
    const val METADATA_CHANGED = "${TAG}_METADATA_CHANGED"
    const val STATE_CHANGED = "${TAG}_STATE_CHANGED"
    const val ACTION_CHANGED = "${TAG}_ACTION_CHANGED"
    const val QUEUE_CHANGED = "${TAG}_QUEUE_CHANGED"

//    arguments metadata
    const val ARGUMENT_SONG_ID = "${TAG}_ARGUMENT_SONG_ID"
    const val ARGUMENT_TITLE = "${TAG}_ARGUMENT_TITLE"
    const val ARGUMENT_SUBTITLE = "${TAG}_ARGUMENT_SUBTITLE"

//    arguments state
    const val ARGUMENT_IS_PLAYING = "${TAG}_ARGUMENT_IS_PLAYING"
    const val ARGUMENT_SHOW_NEXT = "${TAG}_ARGUMENT_SHOW_NEXT"
    const val ARGUMENT_SHOW_PREVIOUS = "${TAG}_ARGUMENT_SHOW_PREVIOUS"
    const val ARGUMENT_BOOKMARK = "${TAG}_ARGUMENT_BOOKMARK"

}