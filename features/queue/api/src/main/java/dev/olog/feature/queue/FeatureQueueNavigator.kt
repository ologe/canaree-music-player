package dev.olog.feature.queue

import androidx.fragment.app.Fragment

interface FeatureQueueNavigator {

    fun queueFragment(): Fragment
    fun queueFragmentTag(): String

}