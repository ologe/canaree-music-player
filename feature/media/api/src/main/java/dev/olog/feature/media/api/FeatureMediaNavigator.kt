package dev.olog.feature.media.api

import android.content.Intent
import android.net.Uri

interface FeatureMediaNavigator {

    fun createIntent(
        action: String,
        data: Uri? = null,
    ): Intent

    fun startService(
        action: String,
        data: Uri? = null,
    )

}