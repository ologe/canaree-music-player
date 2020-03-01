package dev.olog.presentation.animations

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialFadeThrough

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

//////////////////////////////////////////////////////////

fun Fragment.setupExitSharedAnimation() {
    val transition = SharedElementTransitionHold().apply {
        //        duration = DURATION
    }
    exitTransition = transition
    reenterTransition = transition
}

fun Fragment.setupEnterSharedAnimation(context: Context) {
    sharedElementEnterTransition = SharedElementTransition(context).apply {
//        duration = DURATION
    }
}