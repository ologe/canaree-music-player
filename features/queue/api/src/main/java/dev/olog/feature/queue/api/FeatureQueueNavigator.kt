package dev.olog.feature.queue.api

import androidx.fragment.app.Fragment

interface FeatureQueueNavigator {

    fun queueFragment(): Fragment
    fun queueFragmentTag(): String

}