package dev.olog.presentation.main

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import dev.olog.feature.presentation.base.extensions.findViewByIdNotRecursive
import dev.olog.presentation.R
import dev.olog.presentation.offlinelyrics.OfflineLyricsFragment
import dev.olog.scrollhelper.ScrollHelper
import dev.olog.navigation.screens.FragmentScreen

class SuperCerealScrollHelper(
    private val activity: FragmentActivity
) : ScrollHelper(activity, true, false) {

    private val subFragments = listOf(
        FragmentScreen.FOLDERS_NORMAL.tag,
        FragmentScreen.FOLDERS_TREE.tag,
        FragmentScreen.SETTINGS.tag
    )

    private val skipFragments = listOf(
        OfflineLyricsFragment.TAG,
        FragmentScreen.FOLDERS.tag
    )

    override fun updateRecyclerViewPadding(
        fragment: Fragment,
        recyclerView: RecyclerView,
        topPadding: Int,
        bottomPadding: Int
    ) {
        if (fragment.tag?.startsWith(FragmentScreen.DETAIL.tag) == true) {
            return
        }
        super.updateRecyclerViewPadding(fragment, recyclerView, topPadding, bottomPadding)
    }

    override fun findFab(fragment: Fragment): View? {
        return fragment.requireView().findViewById(R.id.fab)
    }

    override fun findRecyclerView(fragment: Fragment): RecyclerView? {
        var recyclerView = fragment.requireView().findViewByIdNotRecursive<RecyclerView>(R.id.list)
        if (recyclerView == null && fragment.tag == FragmentScreen.SETTINGS.tag) {
            // preferences fragment has and internal list called `recycler_view`
            recyclerView = fragment.requireView().findViewByIdNotRecursive(R.id.recycler_view)
        }
        return recyclerView
    }

    override fun findTabLayout(fragment: Fragment): View? {
        return fragment.requireView().findViewByIdNotRecursive(R.id.tabLayout)
    }

    override fun findToolbar(fragment: Fragment): View? {
        if (fragment.tag == FragmentScreen.QUEUE.tag){
            // for some reason when drag and drop in queue fragment, the queue became crazy
            return null
        }
        val view = when (fragment.tag) {
            in subFragments -> fragment.requireParentFragment().requireView()
            else -> fragment.requireView()
        }
        return view.findViewByIdNotRecursive(R.id.toolbar)
    }

    override fun findViewPager(fragment: Fragment): ViewPager2? {
        return null
    }

    override fun findBottomNavigation(): View? {
        return activity.findViewById(R.id.bottomWrapper)
    }

    override fun findBottomSheet(): View? {
        return activity.findViewById(R.id.slidingPanel)
    }

    override fun shouldSkipFragment(fragment: Fragment): Boolean {
        if (fragment.tag in skipFragments) {
            return true
        }
        return isPlayerTag(fragment.tag) || !hasFragmentOwnership(fragment.tag)
    }

    private fun hasFragmentOwnership(tag: String?) = tag?.startsWith(FragmentScreen.OWNERSHIP) == true

    private fun isPlayerTag(tag: String?) = tag?.startsWith(FragmentScreen.PLAYER.tag) == true
}