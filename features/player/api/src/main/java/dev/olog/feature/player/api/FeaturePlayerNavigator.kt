package dev.olog.feature.player.api

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity

interface FeaturePlayerNavigator {

    fun show(
        activity: FragmentActivity,
        @IdRes playerContainer: Int,
        @IdRes miniPlayerContainer: Int,
    )

}