package dev.olog.feature.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SplashFragmentViewPagerAdapter(
    fragmentManager: FragmentManager

) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SplashPresentationFragment()
            1 -> SplashTutorialFragment()
            else -> throw IllegalArgumentException("invalid position $position")
        }
    }

    override fun getCount(): Int = 2
}