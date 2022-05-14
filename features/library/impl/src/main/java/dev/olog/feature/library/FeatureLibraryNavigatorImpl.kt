package dev.olog.feature.library

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dev.olog.feature.library.api.FeatureLibraryNavigator
import dev.olog.feature.library.folder.FolderTreeFragment
import dev.olog.platform.CanHandleOnBackPressed
import javax.inject.Inject

class FeatureLibraryNavigatorImpl @Inject constructor(

) : FeatureLibraryNavigator {

    override fun tracksFragment(): Fragment {
        return LibraryFragment.newInstance(false)
    }

    override fun tracksFragmentTag(): String {
        return LibraryFragment.TAG_TRACK
    }

    override fun podcastsFragment(): Fragment {
        return LibraryFragment.newInstance(true)
    }

    override fun podcastsFragmentTag(): String {
        return LibraryFragment.TAG_PODCAST
    }

    override fun tryPopFolderBack(activity: FragmentActivity): Boolean {
        val categoriesFragment = activity.supportFragmentManager
            .findFragmentByTag(LibraryFragment.TAG_TRACK) as? LibraryFragment ?: return false

        if (categoriesFragment.isCurrentFragmentFolderTree()){
            val folderTree = categoriesFragment.childFragmentManager.fragments
                .find { it is FolderTreeFragment } as? CanHandleOnBackPressed
            return folderTree?.handleOnBackPressed() == true
        }
        return false
    }
}