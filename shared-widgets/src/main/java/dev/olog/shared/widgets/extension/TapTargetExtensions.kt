package dev.olog.shared.widgets.extension

import android.content.Context
import com.getkeepsafe.taptargetview.TapTarget
import dev.olog.shared.android.extensions.colorAccent
import dev.olog.shared.android.extensions.colorBackground

fun TapTarget.tint(context: Context): TapTarget {
    val accentColor = context.colorAccent()
    val backgroundColor = context.colorBackground()

    return this.tintTarget(true)
        .outerCircleColorInt(accentColor)
        .targetCircleColorInt(backgroundColor)
}