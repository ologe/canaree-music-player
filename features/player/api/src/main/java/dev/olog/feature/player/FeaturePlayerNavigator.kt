package dev.olog.feature.player

import androidx.fragment.app.Fragment

interface FeaturePlayerNavigator {

    fun playerFragment(): Fragment

    fun miniPlayerFragment(): Fragment

}