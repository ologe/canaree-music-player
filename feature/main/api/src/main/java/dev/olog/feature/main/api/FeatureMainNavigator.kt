package dev.olog.feature.main.api

import android.content.Intent

interface FeatureMainNavigator {

    companion object {
        private const val TAG = "AppConstants"
        const val ACTION_CONTENT_VIEW = "$TAG.action.content.view"
    }

    fun createContentViewIntent(): Intent

}