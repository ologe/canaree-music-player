package dev.olog.feature.edit.api

import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId

interface FeatureEditNavigator {

    fun toEditInfoFragment(activity: FragmentActivity, mediaId: MediaId)

}