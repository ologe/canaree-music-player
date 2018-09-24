package dev.olog.msc.presentation.library.categories.podcast

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ChildFragmentManager
import dev.olog.msc.presentation.library.tab.TabFragment
import dev.olog.msc.utils.MediaIdCategory
import javax.inject.Inject

class CategoriesPodcastFragmentViewPager @Inject constructor(
        @ApplicationContext private val context: Context,
        @ChildFragmentManager private val fragmentManager: FragmentManager

) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when (position){
            0 -> return TabFragment.newInstance(MediaIdCategory.PODCASTS)
            else -> TabFragment.newInstance(MediaIdCategory.PODCASTS_PLAYLIST)
        }
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence? {
        return context.getString(when(position){
            0 -> R.string.common_podcast
            else -> R.string.common_playlists
        })
    }
}