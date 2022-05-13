package dev.olog.feature.search

import androidx.fragment.app.Fragment

interface FeatureSearchNavigator {

    fun searchFragment(): Fragment
    fun searchFragmentTag(): String

}