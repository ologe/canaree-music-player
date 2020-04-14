package dev.olog.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.transition.MaterialSharedAxis
import dev.olog.navigation.screens.FragmentScreen
import dev.olog.navigation.transition.setupEnterSharedAxisAnimation
import dev.olog.navigation.transition.setupExitSharedAxisAnimation
import javax.inject.Inject
import javax.inject.Provider

internal class LibraryNavigatorImpl @Inject constructor(
    private val fragments: Map<FragmentScreen, @JvmSuppressWildcards Provider<Fragment>>
) : BaseNavigator(), LibraryNavigator {

    override fun toAlbums(activity: FragmentActivity) {
        val fragment = fragments[FragmentScreen.ALBUMS]?.get()
        val tag = FragmentScreen.ALBUMS.tag

        val visibleFragment = findFirstVisibleFragment(activity.supportFragmentManager)
        visibleFragment?.setupExitSharedAxisAnimation(activity, MaterialSharedAxis.X)

        replaceFragment(activity, fragment, tag) {
            addToBackStack(tag)
            it.setupEnterSharedAxisAnimation(activity, MaterialSharedAxis.X)
        }
    }

    override fun toArtists(activity: FragmentActivity) {
        val fragment = fragments[FragmentScreen.ARTISTS]?.get()
        val tag = FragmentScreen.ARTISTS.tag

        val visibleFragment = findFirstVisibleFragment(activity.supportFragmentManager)
        visibleFragment?.setupExitSharedAxisAnimation(activity, MaterialSharedAxis.X)

        replaceFragment(activity, fragment, tag){
            addToBackStack(tag)
            it.setupEnterSharedAxisAnimation(activity, MaterialSharedAxis.X)
        }
    }

    override fun toFolders(activity: FragmentActivity) {
        val fragment = fragments[FragmentScreen.FOLDERS]?.get()
        val tag = FragmentScreen.FOLDERS.tag

        val visibleFragment = findFirstVisibleFragment(activity.supportFragmentManager)
        visibleFragment?.setupExitSharedAxisAnimation(activity, MaterialSharedAxis.X)

        replaceFragment(activity, fragment, tag) {
            addToBackStack(tag)
            it.setupEnterSharedAxisAnimation(activity, MaterialSharedAxis.X)
        }
    }

    override fun toGenres(activity: FragmentActivity) {
        val fragment = fragments[FragmentScreen.GENRES]?.get()
        val tag = FragmentScreen.GENRES.tag

        val visibleFragment = findFirstVisibleFragment(activity.supportFragmentManager)
        visibleFragment?.setupExitSharedAxisAnimation(activity, MaterialSharedAxis.X)

        replaceFragment(activity, fragment, tag) {
            addToBackStack(tag)
            it.setupEnterSharedAxisAnimation(activity, MaterialSharedAxis.X)
        }
    }

}
