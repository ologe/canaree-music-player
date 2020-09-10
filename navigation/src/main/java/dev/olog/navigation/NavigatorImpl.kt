package dev.olog.navigation

import android.content.Intent
import android.media.audiofx.AudioEffect
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import dev.olog.domain.MediaId
import dev.olog.domain.entity.PlaylistType
import dev.olog.navigation.screens.FragmentScreen
import dev.olog.navigation.transition.setupEnterAnimation
import dev.olog.navigation.transition.setupEnterSharedAnimation
import dev.olog.navigation.transition.setupExitAnimation
import dev.olog.navigation.transition.setupExitSharedAnimation
import dev.olog.navigation.utils.ActivityProvider
import dev.olog.navigation.utils.findFirstVisibleFragment
import javax.inject.Inject
import javax.inject.Provider

// TODO (activity as HasSlidingPanel?)?.getSlidingPanel().collapse()
internal class NavigatorImpl @Inject constructor(
    private val activityProvider: ActivityProvider,
    private val fragments: Map<FragmentScreen, @JvmSuppressWildcards Provider<Fragment>>,
    bottomNavigator: BottomNavigatorImpl,
    serviceNavigator: ServiceNavigatorImpl
) : BaseNavigator(),
    Navigator,
    BottomNavigator by bottomNavigator,
    ServiceNavigator by serviceNavigator {

    override fun toFirstAccess() {
        val activity = activityProvider() ?: return
        val fragment = fragments[FragmentScreen.ONBOARDING]?.get()
        val tag = FragmentScreen.ONBOARDING.tag
        replaceFragment(activity, fragment, tag, android.R.id.content, forced = true)
    }

    override fun toPlayer(containerId: Int) {
        return
        val activity = activityProvider() ?: return
        val fragment = fragments[FragmentScreen.PLAYER]?.get()
        val tag = FragmentScreen.PLAYER.tag
        replaceFragment(activity, fragment, tag, containerId, forced = true)
    }

    override fun toMiniPlayer(containerId: Int) {
        return
        val activity = activityProvider() ?: return
        val fragment = fragments[FragmentScreen.PLAYER_MINI]?.get()
        val tag = FragmentScreen.PLAYER_MINI.tag
        replaceFragment(activity, fragment, tag, containerId, forced = true)
    }

    override fun toDetailFragment(mediaId: MediaId.Category, view: View?) {
        val activity = activityProvider() ?: return
        // TODO collapse here
        val fragment = fragments[FragmentScreen.DETAIL]?.get()
        val tag = createBackStackTag(FragmentScreen.DETAIL.tag)

        fragment?.arguments = bundleOf(
            Params.MEDIA_ID to mediaId.toString(),
            Params.CONTAINER_TRANSITION_NAME to (view?.transitionName ?: "")
        )

        val visibleFragment = activity.supportFragmentManager.findFirstVisibleFragment()
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

    override fun toSettings() {
        val activity = activityProvider() ?: return
        val fragment = fragments[FragmentScreen.SETTINGS]?.get()
        val tag = FragmentScreen.SETTINGS.tag
        val current = activity.supportFragmentManager.findFirstVisibleFragment()
        current!!.setupExitAnimation(activity)

        replaceFragment(activity, fragment, tag) {
            it.setupEnterAnimation(activity)
            addToBackStack(tag)
        }

    }

    override fun toAbout() {
        val activity = activityProvider() ?: return
        val fragment = fragments[FragmentScreen.ABOUT]?.get()
        val tag = FragmentScreen.ABOUT.tag
        val current = activity.supportFragmentManager.findFirstVisibleFragment()
        current!!.setupExitAnimation(activity)

        replaceFragment(activity, fragment, tag) {
            it.setupEnterAnimation(activity)
            addToBackStack(tag)
        }
    }

    override fun toEqualizer() {
        val activity = activityProvider() ?: return
        val useCustomEqualizer = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
            .getBoolean(activity.getString(R.string.prefs_used_equalizer_key), true)

        if (useCustomEqualizer) {
            toBuiltInEqualizer()
        } else {
            searchForEqualizer()
        }

    }

    override fun toEdit(mediaId: MediaId) {
        TODO("see old navigator")
    }

    private fun toBuiltInEqualizer() {
        val activity = activityProvider() ?: return
        val fragment = fragments[FragmentScreen.EQUALIZER]?.get()
        val tag = FragmentScreen.EQUALIZER.tag
        replaceFragment(activity, fragment, tag)
    }

    private fun searchForEqualizer() {
        val activity = activityProvider() ?: return
        val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {
            // TODO show snackbar
            Toast.makeText(activity, R.string.equalizer_not_found, Toast.LENGTH_LONG)
                .show()
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