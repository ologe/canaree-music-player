package dev.olog.presentation.splash

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

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