package dev.olog.msc.presentation.splash

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import dev.olog.msc.presentation.splash.presentation.SplashPresentationFragment
import dev.olog.msc.presentation.splash.tutorial.SplashTutorialFragment

class SplashActivityViewPagerAdapter(
        fragmentManager : FragmentManager

) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return when(position){
            0 -> SplashPresentationFragment()
            1 -> SplashTutorialFragment()
            else -> throw IllegalArgumentException("invalid position $position")
        }
    }

    override fun getCount(): Int = 2
}