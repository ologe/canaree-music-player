package dev.olog.msc.presentation.splash

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import dev.olog.msc.presentation.splash.presentation.SplashPresentationFragment
import dev.olog.msc.presentation.splash.tutorial.SplashTutorialFragment
import javax.inject.Inject

class SplashActivityViewPagerAdapter @Inject constructor(
        fragmentManager : FragmentManager

) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> SplashPresentationFragment()
            1 -> SplashTutorialFragment()
            else -> throw IllegalArgumentException("invalid position $position")
        }
    }

    override fun getCount(): Int = 2
}