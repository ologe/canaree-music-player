package dev.olog.ui.extension

import android.content.Context
import com.getkeepsafe.taptargetview.TapTarget
import dev.olog.ui.colorAccent
import dev.olog.ui.colorBackground

fun TapTarget.tint(context: Context): TapTarget {
    val accentColor = context.colorAccent()
    val backgroundColor = context.colorBackground()

    return this.tintTarget(true)
        .outerCircleColorInt(accentColor)
        .targetCircleColorInt(backgroundColor)
}