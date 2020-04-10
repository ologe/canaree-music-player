package dev.olog.navigation

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import dev.olog.domain.MediaId
import dev.olog.domain.entity.PlaylistType

interface Navigator: BottomNavigator, ServiceNavigator {

    companion object {
        const val REQUEST_CODE_HOVER_PERMISSION = 1000

    }

    fun toFirstAccess(activity: FragmentActivity)

    fun toPlayer(
        activity: FragmentActivity,
        @IdRes containerId: Int
    )

    fun toMiniPlayer(
        activity: FragmentActivity,
        @IdRes containerId: Int
    )

    fun toDetailFragment(
        activity: FragmentActivity,
        mediaId: MediaId.Category,
        view: View?
    )

    // TODO shared element??
    fun toSettings(activity: FragmentActivity)

    fun toAbout(activity: FragmentActivity)

    fun toRelatedArtists(
        mediaId: MediaId.Category,
        view: View
    )

    fun toRecentlyAdded(
        mediaId: MediaId.Category,
        view: View
    )

    fun toChooseTracksForPlaylistFragment(
        type: PlaylistType,
        view: View
    )

    fun toEditInfoFragment(mediaId: MediaId)

    fun toOfflineLyrics()

    fun toDialog(
        mediaId: MediaId,
        anchor: View,
        container: View?
    )

//    fun toMainPopup(
//        anchor: View,
//        category: MainPopupCategory
//    )

    fun toSetRingtoneDialog(
        mediaId: MediaId.Track,
        title: String,
        artist: String
    )

    fun toCreatePlaylistDialog(
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

    fun toCreatePlaylistDialogFromPlayingQueue()

    fun toAddToFavoriteDialog(
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

    fun toPlayLater(
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

    fun toPlayNext(
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

    fun toRenameDialog(
        mediaId: MediaId.Category,
        itemTitle: String
    )

    fun toClearPlaylistDialog(
        mediaId: MediaId.Category,
        itemTitle: String
    )

    fun toDeleteDialog(
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

    fun toRemoveDuplicatesDialog(
        mediaId: MediaId.Category,
        itemTitle: String
    )
}