package dev.olog.navigation.transition

import android.content.Context
import androidx.core.transition.doOnEnd
import androidx.fragment.app.Fragment
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialFadeThrough
import dev.olog.navigation.R
import dev.olog.navigation.themeAttributeToColor

//private const val DURATION = 2000L

// Fragment context can't be initialized yet, so don't use `Fragment#requireContext()`

fun Fragment.setupExitAnimation(context: Context) {
    val transition = MaterialFadeThrough.create(context).apply {
//        duration = DURATION
    }
    exitTransition = transition
    reenterTransition = transition
}

fun Fragment.setupEnterAnimation(context: Context) {
    val transition = MaterialFadeThrough.create(context).apply {
//        duration = DURATION
    }
    enterTransition = transition
    returnTransition = transition
}

////////////////////// SHARED ////////////////////////////////////

fun Fragment.setupExitSharedAnimation() {
    val transition = Hold().apply {
        duration = resources.getInteger(R.integer.shared_element_duration).toLong()
    }
    exitTransition = transition
}

fun Fragment.setupEnterSharedAnimation(context: Context) {
    sharedElementEnterTransition = MaterialContainerTransform(context).apply {
        drawingViewId = R.id.fragmentContainer
        containerColor = context.themeAttributeToColor(android.R.attr.colorBackground)
        duration = context.resources.getInteger(R.integer.shared_element_duration).toLong()
    }
    sharedElementReturnTransition = MaterialContainerTransform(context).apply {
        drawingViewId = R.id.fragmentContainer
        containerColor = context.themeAttributeToColor(android.R.attr.colorBackground)
        duration = context.resources.getInteger(R.integer.shared_element_duration).toLong()
    }
}