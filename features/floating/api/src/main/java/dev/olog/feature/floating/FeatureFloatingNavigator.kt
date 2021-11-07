package dev.olog.feature.floating

import android.content.Intent
import androidx.fragment.app.FragmentActivity

interface FeatureFloatingNavigator {

    fun startService(activity: FragmentActivity)

    fun startServiceIfHasPermission(activity: FragmentActivity)

    fun handleOnActivityResult(
        activity: FragmentActivity,
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ): Boolean

}