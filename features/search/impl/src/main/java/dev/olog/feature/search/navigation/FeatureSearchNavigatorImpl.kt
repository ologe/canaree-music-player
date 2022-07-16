package dev.olog.feature.search.navigation

import androidx.fragment.app.Fragment
import dev.olog.feature.search.SearchFragment
import dev.olog.feature.search.api.FeatureSearchNavigator
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