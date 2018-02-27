package dev.olog.msc.constants

object PlaylistConstants {

    const val LAST_ADDED_ID: Long = -3000
    const val FAVORITE_LIST_ID: Long = -30012
    const val HISTORY_LIST_ID: Long = -30018

    const val MINI_QUEUE_SIZE = 50

    private val autoPlaylists = listOf(
            LAST_ADDED_ID, FAVORITE_LIST_ID, HISTORY_LIST_ID
    )

    fun isAutoPlaylist(id: Long) = autoPlaylists.contains(id)

}