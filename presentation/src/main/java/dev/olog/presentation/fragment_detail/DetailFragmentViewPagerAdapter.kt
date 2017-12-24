package dev.olog.presentation.fragment_detail

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import dev.olog.presentation.R
import dev.olog.presentation.dagger.NestedFragmentManager
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_detail.most_played.DetailMostPlayedFragment
import dev.olog.presentation.fragment_detail.recently_added.DetailRecentlyAddedFragment
import dev.olog.shared.ApplicationContext
import javax.inject.Inject


@PerFragment
class DetailFragmentViewPagerAdapter @Inject constructor(
        @ApplicationContext private val context: Context,
        private val mediaId: String,
        @NestedFragmentManager fragmentManager: FragmentManager

) : FragmentStatePagerAdapter(fragmentManager) {

    private var currentPosition = -1

    override fun getItem(position: Int): Fragment? {
        return when (position){
            0 -> DetailMostPlayedFragment.newInstance(mediaId)
            1 -> DetailRecentlyAddedFragment.newInstance(mediaId)
            else -> null
        }
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position){
            0 -> context.getString(R.string.detail_most_played)
            1 -> context.getString(R.string.detail_recently_added)
            else -> null
        }
    }

}