package dev.olog.feature.queue.navigation

import androidx.fragment.app.Fragment
import dev.olog.feature.queue.PlayingQueueFragment
import dev.olog.feature.queue.api.FeatureQueueNavigator
import javax.inject.Inject

class FeatureQueueNavigatorImpl @Inject constructor(

) : FeatureQueueNavigator {

    override fun queueFragment(): Fragment {
        return PlayingQueueFragment()
    }

    override fun queueFragmentTag(): String {
        return PlayingQueueFragment.TAG
    }
}