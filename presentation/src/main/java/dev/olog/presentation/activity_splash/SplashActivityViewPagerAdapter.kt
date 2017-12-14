package dev.olog.presentation.activity_splash

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import javax.inject.Inject

class SplashActivityViewPagerAdapter @Inject constructor(
        fragmentManager : FragmentManager

) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> SplashFragment()
            1 -> SplashFragmentTutorial()
            else -> throw IllegalArgumentException("invalid position $position")
        }
    }

    override fun getCount(): Int = 2
}