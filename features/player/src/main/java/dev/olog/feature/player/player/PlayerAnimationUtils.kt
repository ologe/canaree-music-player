package dev.olog.feature.player.player

import android.view.View

internal fun View.rotate(degrees: Float, duration: Long = 200) {
    animate().cancel()
    animate().rotation(degrees)
        .setDuration(duration)
        .withEndAction { animate().rotation(0f).setDuration(duration) }
}