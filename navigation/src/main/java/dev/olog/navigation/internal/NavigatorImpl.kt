package dev.olog.navigation.internal

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType
import dev.olog.navigation.BottomNavigator
import dev.olog.navigation.Navigator
import dev.olog.navigation.Params
import dev.olog.navigation.R
import dev.olog.navigation.destination.FragmentScreen
import dev.olog.navigation.utils.findFirstVisibleFragment
import javax.inject.Inject
import javax.inject.Provider

internal class NavigatorImpl @Inject constructor(
    bottomNavigator: BottomNavigatorImpl,
    private val activityProvider: ActivityProvider,
    private val fragments: Map<FragmentScreen, @JvmSuppressWildcards Provider<Fragment>>,
) : BaseNavigator(),
    Navigator,
    BottomNavigator by bottomNavigator {

    override fun toFirstAccess() {
        navigate(
            FragmentScreen.ONBOARDING,
            containerId = android.R.id.content,
            forced = true
        )
    }

    override fun toDetailFragment(mediaId: MediaId) {
        // TODO collapse sliding panel here
        navigateToDetail(FragmentScreen.DETAIL, mediaId)
    }

    override fun toRelatedArtists(mediaId: MediaId) {
        navigateToDetail(FragmentScreen.RELATED_ARTISTS, mediaId)
    }

    override fun toRecentlyAdded(mediaId: MediaId) {
        navigateToDetail(FragmentScreen.RECENTLY_ADDED, mediaId)
    }

    override fun toDialog(mediaId: MediaId, view: View) {
        TODO("Not yet implemented")
    }

    override fun toChooseTracksForPlaylistFragment(type: PlaylistType) {
        TODO("Not yet implemented")
    }

    override fun toCreatePlaylist(
//        mediaId: MediaId, listSize: Int, itemTitle: String
    ) {
        TODO("Not yet implemented")
    }

    override fun toLibraryPreferences(isPodcast: Boolean) {
        val activity = activityProvider() ?: return
        val fragment = fragments[FragmentScreen.LIBRARY_PREFS]?.get()

        fragment?.arguments = bundleOf(
            Params.IS_PODCAST to isPodcast
        )

        addFragment(activity, fragment, FragmentScreen.LIBRARY_PREFS.tag)
    }

    override fun toAbout() {
        navigate(screen = FragmentScreen.ABOUT)
    }

    override fun toEqualizer() {
        navigate(screen = FragmentScreen.EQUALIZER)
//        val useCustomEqualizer = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext) TODO
//            .getBoolean(activity.getString(R.string.prefs_used_equalizer_key), true)
//
//        if (useCustomEqualizer) {
//            toBuiltInEqualizer()
//        } else {
//            searchForEqualizer()
//        }
    }

//    private fun toBuiltInEqualizer() {
//        val instance = EqualizerFragment.newInstance()
//        instance.show(activity.supportFragmentManager, EqualizerFragment.TAG)
//    }
//
//    private fun searchForEqualizer() {
//        val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
//        if (intent.resolveActivity(activity.packageManager) != null) {
//            activity.startActivity(intent)
//        } else {
//            activity.toast(R.string.equalizer_not_found)
//        }
//    }

    override fun toSettings() {
        navigate(screen = FragmentScreen.SETTINGS)
    }

    override fun toSleepTimer() {
        navigate(screen = FragmentScreen.SLEEP_TIMER)
    }

    private fun navigate(
        screen: FragmentScreen,
        bundle: Bundle? = null,
        forced: Boolean = false,
        @IdRes containerId: Int = R.id.fragmentContainer,
    ) {
        val activity = activityProvider() ?: return
        val fragment = fragments[screen]?.get()
        val tag = screen.tag

        fragment?.arguments = bundle

        replaceFragment(
            activity = activity,
            fragment = fragment,
            tag = tag,
            containerId = containerId, forced = forced)
    }

    private fun navigateToDetail(screen: FragmentScreen, mediaId: MediaId) {
        val activity = activityProvider() ?: return
        val fragment = fragments[screen]?.get()
        val tag = createBackStackTag(screen.tag)

        fragment?.arguments = bundleOf(
            Params.MEDIA_ID to mediaId.toString(),
        )

        val topFragment = activity.supportFragmentManager.findFirstVisibleFragment()
        addFragment(activity, fragment, tag) {
            addToBackStack(tag)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            topFragment?.let { hide(it) }
        }
    }

}