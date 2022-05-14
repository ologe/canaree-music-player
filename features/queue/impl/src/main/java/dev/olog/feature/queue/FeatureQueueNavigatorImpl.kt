package dev.olog.feature.queue

import androidx.fragment.app.Fragment
import dev.olog.feature.queue.api.FeatureQueueNavigator
import javax.inject.Inject

class FeatureQueueNavigatorImpl @Inject constructor(

) : FeatureQueueNavigator {

    override fun queueFragment(): Fragment {
        return PlayingQueueFragment.newInstance()
    }

    override fun queueFragmentTag(): String {
        return PlayingQueueFragment.TAG
    }
}