package dev.olog.navigation

import android.view.View
import androidx.annotation.IdRes
import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType

interface Navigator : BottomNavigator, AboutNavigator {

    fun toFirstAccess()

    fun toPlayer(@IdRes containerId: Int)
    fun toMiniPlayer(@IdRes containerId: Int)

    fun toDetailFragment(mediaId: MediaId)

    fun toRecentlyAdded(mediaId: MediaId)

    fun toRelatedArtists(mediaId: MediaId)

    fun toChooseTracksForPlaylistFragment(type: PlaylistType)

    fun toLibraryPreferences(isPodcast: Boolean)

    fun toAbout()

    fun toEqualizer()

    fun toSettings()

    fun toSleepTimer()

    fun toDialog(mediaId: MediaId, view: View)

    fun toCreatePlaylist()

    fun toOfflineLyrics()

}