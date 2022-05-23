package dev.olog.compose

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

val isLandscape: Boolean
    @Composable
    get() {
        val configuration = LocalConfiguration.current
        return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

val isLargeScreen: Boolean
    @Composable
    get() {
        val configuration = LocalConfiguration.current
        return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE ||
            configuration.smallestScreenWidthDp > 600
    }