package dev.olog.navigation

import android.content.Intent
import android.media.audiofx.AudioEffect
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import dev.olog.domain.MediaId
import dev.olog.domain.entity.PlaylistType
import dev.olog.navigation.screens.FragmentScreen
import dev.olog.navigation.transition.setupEnterFadeAnimation
import dev.olog.navigation.transition.setupSharedAnimation
import dev.olog.navigation.transition.setupExitFadeAnimation
import dev.olog.navigation.transition.setupExitSharedAnimation
import javax.inject.Inject
import javax.inject.Provider

// TODO (activity as HasSlidingPanel?)?.getSlidingPanel().collapse()
internal class NavigatorImpl @Inject constructor(
    private val fragments: Map<FragmentScreen, @JvmSuppressWildcards Provider<Fragment>>,
    bottomNavigator: BottomNavigatorImpl,
    serviceNavigator: ServiceNavigatorImpl,
    libraryNavigator: LibraryNavigatorImpl
) : BaseNavigator(),
    Navigator,
    BottomNavigator by bottomNavigator,
    ServiceNavigator by serviceNavigator,
    LibraryNavigator by libraryNavigator {

    override fun toFirstAccess(activity: FragmentActivity) {
        val fragment = fragments[FragmentScreen.ONBOARDING]?.get()
        val tag = FragmentScreen.ONBOARDING.tag
        replaceFragment(activity, fragment, tag, android.R.id.content, forced = true)
    }

    override fun toPlayer(activity: FragmentActivity, containerId: Int) {
        val fragment = fragments[FragmentScreen.PLAYER]?.get()
        val tag = FragmentScreen.PLAYER.tag
        replaceFragment(activity, fragment, tag, containerId, forced = true)
    }

    override fun toMiniPlayer(activity: FragmentActivity, containerId: Int) {
        val fragment = fragments[FragmentScreen.PLAYER_MINI]?.get()
        val tag = FragmentScreen.PLAYER_MINI.tag
        replaceFragment(activity, fragment, tag, containerId, forced = true)
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
            visibleFragment?.setupExitFadeAnimation(activity)
        } else {
            visibleFragment?.setupExitSharedAnimation()
        }

        replaceFragment(activity, fragment, tag) {
            addToBackStack(tag)

            if (view == null) {
                it.setupEnterFadeAnimation(activity)
            } else {
                it.setupSharedAnimation(activity)
                addSharedElement(view, view.transitionName)

            }
        }
    }

    override fun toSettings(activity: FragmentActivity) {
        val fragment = fragments[FragmentScreen.SETTINGS]?.get()
        val tag = FragmentScreen.SETTINGS.tag
        val current = findFirstVisibleFragment(activity.supportFragmentManager)
        current!!.setupExitFadeAnimation(activity)

        replaceFragment(activity, fragment, tag) {
            it.setupEnterFadeAnimation(activity)
            addToBackStack(tag)
        }

    }

    override fun toAbout(activity: FragmentActivity) {
        val fragment = fragments[FragmentScreen.ABOUT]?.get()
        val tag = FragmentScreen.ABOUT.tag
        val current = findFirstVisibleFragment(activity.supportFragmentManager)
        current!!.setupExitFadeAnimation(activity)

        replaceFragment(activity, fragment, tag) {
            it.setupEnterFadeAnimation(activity)
            addToBackStack(tag)
        }
    }

    override fun toEqualizer(activity: FragmentActivity) {
        val useCustomEqualizer = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
            .getBoolean(activity.getString(R.string.prefs_used_equalizer_key), true)

        if (useCustomEqualizer) {
            toBuiltInEqualizer(activity)
        } else {
            searchForEqualizer(activity)
        }

    }

    override fun toEdit(activity: FragmentActivity, mediaId: MediaId) {
        TODO("see old navigator")
    }

    private fun toBuiltInEqualizer(activity: FragmentActivity) {
        val fragment = fragments[FragmentScreen.EQUALIZER]?.get()
        val tag = FragmentScreen.EQUALIZER.tag
        replaceFragment(activity, fragment, tag)
    }

    private fun searchForEqualizer(activity: FragmentActivity) {
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