package dev.olog.navigation

import android.view.View
import androidx.annotation.IdRes
import dev.olog.core.mediaid.MediaId
import dev.olog.core.entity.PlaylistType

interface Navigator :
    BottomNavigator,
    AboutNavigator,
    ServiceNavigator {

    companion object {
        const val START_SERVICE_ACTION = "start.service"
        const val INTENT_ACTION_SEARCH = "intent.search"
        const val INTENT_ACTION_DETAIL = "intent.detail"
        const val INTENT_ACTION_CONTENT_VIEW = "intent.content.view"
        const val HOVER_CODE = 123
    }

    fun toFirstAccess()

    fun toPlayer(@IdRes containerId: Int)
    fun toMiniPlayer(@IdRes containerId: Int)

    fun toDetailFragment(mediaId: MediaId)

    fun toRecentlyAdded(mediaId: MediaId)

    fun toRelatedArtists(mediaId: MediaId)

    fun toEditInfo(mediaId: MediaId)

    fun toChooseTracksForPlaylistFragment(type: PlaylistType)

    fun toLibraryPreferences(isPodcast: Boolean)

    fun toAbout()

    fun toEqualizer()

    fun toSettings()

    fun toSleepTimer()

    fun toDialog(mediaId: MediaId, view: View)

    fun toCreatePlaylist()

    fun toOfflineLyrics()

    fun toBlacklist()

    fun toCreatePlaylist(mediaId: MediaId, songs: Int, title: String)
    fun toPlayLater(mediaId: MediaId, songs: Int, title: String)
    fun toPlayNext(mediaId: MediaId, songs: Int, title: String)
    fun toAddToFavorite(mediaId: MediaId, songs: Int, title: String)
    fun toRename(mediaId: MediaId, title: String)
    fun toDelete(mediaId: MediaId, songs: Int, title: String)
    fun toRemoveDuplicates(mediaId: MediaId, title: String)
    fun toClearPlaylist(mediaId: MediaId, title: String)

}