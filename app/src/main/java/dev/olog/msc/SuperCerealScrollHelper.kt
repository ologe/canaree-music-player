package dev.olog.msc

import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.detail.main.DetailFragment
import dev.olog.feature.library.LibraryFragment
import dev.olog.feature.library.folder.FolderTreeFragment
import dev.olog.feature.player.main.PlayerFragment
import dev.olog.feature.queue.PlayingQueueFragment
import dev.olog.feature.settings.SettingsFragment
import dev.olog.platform.navigation.FragmentTagFactory
import dev.olog.scrollhelper.ScrollHelper
import dev.olog.scrollhelper.ScrollType
import dev.olog.shared.extension.findViewByIdNotRecursive
import dev.olog.shared.extension.setMargin
import dev.olog.ui.ScrollHelperFactory

// keeping it in :app for simplicity because it has to know about a lot of fragments
class SuperCerealScrollHelper(
    activity: FragmentActivity,
    input: ScrollType
) : ScrollHelper(activity, input, false, false, false) { // debug is very slow

    override fun applyInsetsToList(fragment: Fragment, list: RecyclerView, toolbar: View?, tabLayout: View?) {
        super.applyInsetsToList(fragment, list, toolbar, tabLayout)
        if (fragment is DetailFragment) {
            // apply only top padding
            list.updatePadding(top = 0)
        }
        if (fragment is FolderTreeFragment){
            val crumbsWrapper = fragment.view!!.findViewById<View>(dev.olog.feature.library.R.id.crumbsWrapper)
            if (crumbsWrapper.marginTop < 1){
//                 margin not set yet
                fragment.view!!.doOnPreDraw {
                    crumbsWrapper.setMargin(top = toolbar!!.height + tabLayout!!.height)
                    list.updatePadding(top = list.paddingTop + crumbsWrapper!!.height)
                }
            }
        }
    }

    override fun searchForFab(fragment: Fragment): View? {
        return fragment.view?.findViewById(dev.olog.feature.library.R.id.fab)
    }

    override fun searchForRecyclerView(fragment: Fragment): RecyclerView? {
        var recyclerView = fragment.view?.findViewByIdNotRecursive<RecyclerView>(dev.olog.feature.library.R.id.list)
        if (recyclerView == null && fragment is SettingsFragment) {
            recyclerView = fragment.view?.findViewById(androidx.preference.R.id.recycler_view)
        }
        return recyclerView
    }

    override fun searchForTabLayout(fragment: Fragment): View? {
        val view : View? = when {
            FragmentTagFactory.isFromViewPager(fragment.tag) -> {
                // search toolbar and tab layout in parent fragment
                fragment.parentFragment?.view
            }
            else -> fragment.view
        }
        return view?.findViewByIdNotRecursive(dev.olog.feature.library.R.id.tabLayout)
    }

    override fun searchForToolbar(fragment: Fragment): View? {
        if (fragment is PlayingQueueFragment){
            // for some reason when drag and drop in queue fragment, the queue became crazy
            return null
        }
        val view : View? = when {
            FragmentTagFactory.isFromViewPager(fragment.tag) -> {
                // search toolbar and tab layout in parent fragment
                fragment.parentFragment?.view
            }
            fragment is SettingsFragment -> fragment.parentFragment?.view
            else -> fragment.view
        }
        return view?.findViewByIdNotRecursive(dev.olog.feature.library.R.id.toolbar)
    }

    override fun searchForViewPager(fragment: Fragment): ViewPager? {
        if (fragment is LibraryFragment) {
            return fragment.view?.findViewByIdNotRecursive(dev.olog.feature.library.R.id.viewPager)
        }
        return null
    }

    override fun skipFragment(fragment: Fragment): Boolean {
        if (FragmentTagFactory.isFromViewPager(fragment.tag)){
            return false
        }
        return fragment is PlayerFragment || !hasFragmentOwnership(fragment.tag)
    }

    private fun hasFragmentOwnership(tag: String?) = FragmentTagFactory.isFromFactory(tag)
}

@Module
@InstallIn(SingletonComponent::class)
class ScrollHelperModule {

    @Provides
    fun provideFactory() = ScrollHelperFactory { activity, type ->
        SuperCerealScrollHelper(activity, type)
    }


}