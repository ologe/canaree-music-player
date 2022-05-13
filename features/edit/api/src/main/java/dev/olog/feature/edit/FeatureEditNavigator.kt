package dev.olog.feature.edit

import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId

interface FeatureEditNavigator {

    fun toEditInfoFragment(activity: FragmentActivity, mediaId: MediaId)

}