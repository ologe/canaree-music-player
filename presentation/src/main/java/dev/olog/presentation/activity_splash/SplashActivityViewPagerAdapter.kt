package dev.olog.presentation.activity_splash

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import dev.olog.presentation.R
import javax.inject.Inject

class SplashActivityViewPagerAdapter @Inject constructor(
        fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        val layoutRes = when (position){
            0 -> R.layout.fragment_splash
            1 -> R.layout.fragment_splash_tutorial
            else -> throw IllegalArgumentException("invalid page position $position")
        }
        return DummyFragment.newInstance(layoutRes)
    }

    override fun getCount(): Int = 2
}