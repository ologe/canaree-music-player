package dev.olog.msc.presentation.splash

import dev.olog.msc.presentation.splash.presentation.SplashPresentationFragment
import dev.olog.msc.presentation.splash.tutorial.SplashTutorialFragment
import javax.inject.Inject

class SplashActivityViewPagerAdapter @Inject constructor(
        fragmentManager : androidx.fragment.app.FragmentManager

) : androidx.fragment.app.FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return when(position){
            0 -> SplashPresentationFragment()
            1 -> SplashTutorialFragment()
            else -> throw IllegalArgumentException("invalid position $position")
        }
    }

    override fun getCount(): Int = 2
}