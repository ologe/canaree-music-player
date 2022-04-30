package dev.olog.feature.bubble

import android.app.Activity

interface FeatureBubbleNavigator {

    companion object {
        const val REQUEST_CODE_HOVER_PERMISSION = 1000
    }

    fun startServiceOrRequestOverlayPermission(activity: Activity)

    fun startServiceIfHasOverlayPermission(activity: Activity)

}