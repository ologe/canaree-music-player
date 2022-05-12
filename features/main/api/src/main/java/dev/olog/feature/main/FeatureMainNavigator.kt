package dev.olog.feature.main

import android.view.View
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory

interface FeatureMainNavigator {

    fun toMainPopup(
        activity: FragmentActivity,
        anchor: View,
        category: MediaIdCategory?,
    )

    fun toSleepTimerDialog(activity: FragmentActivity)

    fun toItemDialog(
        activity: FragmentActivity,
        anchor: View,
        mediaId: MediaId,
    )

}