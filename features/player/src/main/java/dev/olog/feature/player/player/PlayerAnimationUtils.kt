package dev.olog.feature.player.player

import android.view.View
import java.lang.ref.WeakReference
import kotlin.time.Duration
import kotlin.time.milliseconds

internal fun View.rotate(degrees: Float, duration: Duration = 200.milliseconds) {
    val weakView = WeakReference(this)

    animate().rotation(degrees)
        .setDuration(duration.toLongMilliseconds())
        .withEndAction {
            weakView.get()
                ?.animate()
                ?.rotation(0f)
                ?.setDuration(duration.toLongMilliseconds())
        }
}