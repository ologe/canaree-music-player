package dev.olog.navigation

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dev.olog.domain.MediaId
import dev.olog.domain.entity.PlaylistType
import dev.olog.navigation.screens.FragmentScreen
import dev.olog.navigation.transition.setupEnterAnimation
import dev.olog.navigation.transition.setupEnterSharedAnimation
import dev.olog.navigation.transition.setupExitAnimation
import dev.olog.navigation.transition.setupExitSharedAnimation
import javax.inject.Inject
import javax.inject.Provider

// TODO (activity as HasSlidingPanel?)?.getSlidingPanel().collapse()
internal class NavigatorImpl @Inject constructor(
    private val fragments: Map<FragmentScreen, @JvmSuppressWildcards Provider<Fragment>>,
    bottomNavigator: BottomNavigatorImpl,
    serviceNavigator: ServiceNavigatorImpl
) : BaseNavigator(),
    Navigator,
    BottomNavigator by bottomNavigator,
    ServiceNavigator by serviceNavigator {

    override fun toFirstAccess() {

    }

    override fun toPlayer(activity: FragmentActivity, containerId: Int) {
        val fragment = fragments[FragmentScreen.PLAYER]?.get()
        replaceFragment(activity, fragment, FragmentScreen.PLAYER.tag, containerId) {}
    }

    override fun toMiniPlayer(activity: FragmentActivity, containerId: Int) {
        val fragment = fragments[FragmentScreen.PLAYER_MINI]?.get()
        replaceFragment(activity, fragment, FragmentScreen.PLAYER_MINI.tag, containerId) {}
    }

    override fun toDetailFragment(
        activity: FragmentActivity,
        mediaId: MediaId.Category,
        view: View?
    ) {
        // TODO collapse here
        val fragment = fragments[FragmentScreen.DETAIL]?.get()
        val tag = createBackStackTag(FragmentScreen.DETAIL.tag)

        fragment?.arguments = bundleOf(
            Params.MEDIA_ID to mediaId.toString(),
            Params.CONTAINER_TRANSITION_NAME to (view?.transitionName ?: "")
        )

        val visibleFragment = findFirstVisibleFragment(activity.supportFragmentManager)
        if (view == null) {
            visibleFragment?.setupExitAnimation(activity)
        } else {
            visibleFragment?.setupExitSharedAnimation()
        }

        replaceFragment(activity, fragment, tag) {
            addToBackStack(tag)

            if (view == null) {
                it.setupEnterAnimation(activity)
            } else {
                it.setupEnterSharedAnimation(activity)
                addSharedElement(view, view.transitionName)

            }
        }
    }

    override fun toRelatedArtists(mediaId: MediaId.Category, view: View) {

    }

    override fun toRecentlyAdded(mediaId: MediaId.Category, view: View) {

    }

    override fun toChooseTracksForPlaylistFragment(type: PlaylistType, view: View) {

    }

    override fun toEditInfoFragment(mediaId: MediaId) {

    }

    override fun toOfflineLyrics() {

    }

    override fun toDialog(mediaId: MediaId, anchor: View, container: View?) {

    }

    override fun toSetRingtoneDialog(mediaId: MediaId.Track, title: String, artist: String) {

    }

    override fun toCreatePlaylistDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {

    }

    override fun toCreatePlaylistDialogFromPlayingQueue() {

    }

    override fun toAddToFavoriteDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {

    }

    override fun toPlayLater(mediaId: MediaId, listSize: Int, itemTitle: String) {

    }

    override fun toPlayNext(mediaId: MediaId, listSize: Int, itemTitle: String) {

    }

    override fun toRenameDialog(mediaId: MediaId.Category, itemTitle: String) {

    }

    override fun toClearPlaylistDialog(mediaId: MediaId.Category, itemTitle: String) {

    }

    override fun toDeleteDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {

    }

    override fun toRemoveDuplicatesDialog(mediaId: MediaId.Category, itemTitle: String) {

    }
}