package dev.olog.presentation.activity_main

import android.content.res.Resources
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import dev.olog.presentation.R
import dev.olog.presentation.fragment_tab.TabFragment
import javax.inject.Inject

class TabViewPagerAdapter @Inject constructor(
        resources: Resources,
        fragmentManager: FragmentManager

) : FragmentPagerAdapter(fragmentManager) {

    companion object {
        val ITEM_COUNT = 6

        const val FOLDER = 0
        const val PLAYLIST = 1
        const val SONG = 2
        const val ALBUM = 3
        const val ARTIST = 4
        const val GENRE = 5
    }

    private val titles = resources.getStringArray(R.array.view_pager_tabs)

    override fun getItem(position: Int): Fragment {
        return TabFragment.newInstance(position)
    }

    override fun getCount(): Int = ITEM_COUNT

    override fun getPageTitle(position: Int): CharSequence = titles[position]

}
