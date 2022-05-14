package dev.olog.feature.search.api

import androidx.fragment.app.Fragment

interface FeatureSearchNavigator {

    fun searchFragment(): Fragment
    fun searchFragmentTag(): String

}