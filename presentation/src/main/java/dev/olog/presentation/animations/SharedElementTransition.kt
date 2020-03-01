package dev.olog.presentation.animations

import android.content.Context
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.colorBackground

class SharedElementTransitionHold : Hold()

class SharedElementTransition(
    context: Context
) : MaterialContainerTransform(context) {

    init {
        containerColor = context.colorBackground()
        drawingViewId = R.id.sharedElementContainer
    }

}