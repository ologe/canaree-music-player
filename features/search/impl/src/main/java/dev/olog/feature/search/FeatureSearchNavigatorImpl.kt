package dev.olog.feature.search

import androidx.fragment.app.Fragment
import javax.inject.Inject

class FeatureSearchNavigatorImpl @Inject constructor(

) : FeatureSearchNavigator {

    override fun searchFragment(): Fragment {
        return SearchFragment.newInstance()
    }

    override fun searchFragmentTag(): String {
        return SearchFragment.TAG
    }
}