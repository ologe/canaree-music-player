package dev.olog.feature.media.helper

internal object CarHelper {

    val AUTO_APP_PACKAGE_NAME = "com.google.android.projection.gearhead"

    /**
     * Action for an intent broadcast by Android Auto when a media app is connected or
     * disconnected. A "connected" media app is the one currently attached to the "media" facet
     * on Android Auto. So, this intent is sent by AA on:
     *
     * - connection: when the phone is projecting and at the moment the app is selected from the
     * list of media apps
     * - disconnection: when another media app is selected from the list of media apps or when
     * the phone stops projecting (when the user unplugs it, for example)
     *
     * The actual event (connected or disconnected) will come as an Intent extra,
     * with the key MEDIA_CONNECTION_STATUS (see below).
     */
    val ACTION_MEDIA_STATUS = "com.google.android.gms.car.media.STATUS"

    /**
     * Key in Intent extras that contains the media connection event type (connected or disconnected)
     */
    val MEDIA_CONNECTION_STATUS = "media_connection_status"

    /**
     * Value of the key MEDIA_CONNECTION_STATUS in Intent extras used when the current media app
     * is connected.
     */
    val MEDIA_CONNECTED = "media_connected"


    fun isValidCarPackage(packageName: String): Boolean {
        return AUTO_APP_PACKAGE_NAME == packageName
    }

    /** Declares that ContentStyle is supported */
    val CONTENT_STYLE_SUPPORTED = "android.media.browse.CONTENT_STYLE_SUPPORTED"

    /**
     * Bundle extra indicating the presentation hint for playable media items.
     */
    val CONTENT_STYLE_PLAYABLE_HINT = "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT"

    /**
     * Bundle extra indicating the presentation hint for browsable media items.
     */
    val CONTENT_STYLE_BROWSABLE_HINT = "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT"

    /**
     * Specifies the corresponding items should be presented as lists.
     */
    val CONTENT_STYLE_LIST_ITEM_HINT_VALUE = 1

    /**
     * Specifies that the corresponding items should be presented as grids.
     */
    val CONTENT_STYLE_GRID_ITEM_HINT_VALUE = 2

    // Bundle extra indicating that onSearch() is supported
    val EXTRA_MEDIA_SEARCH_SUPPORTED = "android.media.browse.SEARCH_SUPPORTED"

}