package dev.olog.feature.library.api

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

interface FeatureLibraryNavigator {

    fun tracksFragment(): Fragment
    fun tracksFragmentTag(): String

    fun podcastsFragment(): Fragment
    fun podcastsFragmentTag(): String

    // todo quick solution, refactor?
    fun tryPopFolderBack(activity: FragmentActivity): Boolean

}