package dev.olog.feature.presentation.base.fragment

import android.content.Context
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform
import dev.olog.feature.presentation.base.R
import dev.olog.shared.android.extensions.colorBackground

class SharedElementTransitionHold : Hold()

class SharedElementTransition(
    context: Context
) : MaterialContainerTransform(context) {

    init {
        containerColor = context.colorBackground()
        drawingViewId = R.id.fragmentContainer
    }

}