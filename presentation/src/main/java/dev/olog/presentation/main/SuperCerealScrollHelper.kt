package dev.olog.presentation.main

import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import dev.olog.presentation.R
import dev.olog.feature.detail.DetailFragment
import dev.olog.presentation.offlinelyrics.OfflineLyricsFragment
import dev.olog.presentation.prefs.SettingsFragment
import dev.olog.presentation.queue.PlayingQueueFragment
import dev.olog.scrollhelper.ScrollHelper
import dev.olog.scrollhelper.ScrollType
import dev.olog.feature.presentation.base.extensions.findViewByIdNotRecursive
import dev.olog.navigation.screens.FragmentScreen

class SuperCerealScrollHelper(
    activity: FragmentActivity,
    input: ScrollType
) : ScrollHelper(activity, input, false, false, false) { // debug is very slow

    override fun applyInsetsToList(fragment: Fragment, list: RecyclerView, toolbar: View?, tabLayout: View?) {
        super.applyInsetsToList(fragment, list, toolbar, tabLayout)
        if (fragment.tag?.startsWith(DetailFragment.TAG) == true){
            // apply only top padding
            list.updatePadding(top = 0)
        }

//        if (fragment.tag is FolderTreeFragment){ TODO
//            val crumbsWrapper = fragment.requireView().findViewById<View>(R.id.crumbsWrapper)
//            if (crumbsWrapper.marginTop < 1){
//                 margin not set yet
//                fragment.requireView().doOnPreDraw {
//                    crumbsWrapper.setMargin(top = toolbar!!.height + tabLayout!!.height)
//                    list.updatePadding(top = list.paddingTop + crumbsWrapper!!.height)
//                }
//            }
//        }
    }

    override fun searchForFab(fragment: Fragment): View? {
        return fragment.view?.findViewById(R.id.fab)
    }

    override fun searchForRecyclerView(fragment: Fragment): RecyclerView? {
        var recyclerView = fragment.view?.findViewByIdNotRecursive<RecyclerView>(R.id.list)
        if (recyclerView == null && fragment.tag == SettingsFragment.TAG) {
            recyclerView = fragment.view?.findViewById(R.id.recycler_view)
        }
        return recyclerView
    }

    override fun searchForTabLayout(fragment: Fragment): View? {
        val view : View? = when {
            isViewPagerChildTag(fragment.tag) -> {
                // search toolbar and tab layout in parent fragment
                fragment.parentFragment?.view
            }
            else -> fragment.view
        }
        return view?.findViewByIdNotRecursive(R.id.tabLayout)
    }

    override fun searchForToolbar(fragment: Fragment): View? {
        if (fragment.tag == PlayingQueueFragment.TAG){
            // for some reason when drag and drop in queue fragment, the queue became crazy
            return null
        }
        val view : View? = when {
            isViewPagerChildTag(fragment.tag) -> {
                // search toolbar and tab layout in parent fragment
                fragment.parentFragment?.view
            }
            fragment.tag == SettingsFragment.TAG -> fragment.parentFragment?.view
            else -> fragment.view
        }
        return view?.findViewByIdNotRecursive(R.id.toolbar)
    }

    override fun searchForViewPager(fragment: Fragment): ViewPager? {
        val tag = fragment.tag
        if (tag == FragmentScreen.LIBRARY_TRACKS.tag || tag == FragmentScreen.LIBRARY_PODCAST.tag) {
            return fragment.view?.findViewByIdNotRecursive(R.id.viewPager)
        }
        return null
    }

    override fun skipFragment(fragment: Fragment): Boolean {
        if (isViewPagerChildTag(fragment.tag)){
            return false
        }
        if (fragment.tag == OfflineLyricsFragment.TAG) {
            return true
        }
        return isPlayerTag(fragment.tag) || !hasFragmentOwnership(fragment.tag)
    }

    private fun isViewPagerChildTag(tag: String?) = tag?.startsWith("android:switcher:") == true

    private fun hasFragmentOwnership(tag: String?) = tag?.startsWith("dev.olog") == true

    private fun isPlayerTag(tag: String?) = tag?.contains("Player") == true
}