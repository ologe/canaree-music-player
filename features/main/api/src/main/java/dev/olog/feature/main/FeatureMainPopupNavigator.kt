package dev.olog.feature.main

import android.view.View
import dev.olog.core.MediaId

interface FeatureMainPopupNavigator {

    fun toItemDialog(
        anchor: View,
        mediaId: MediaId,
    )

}