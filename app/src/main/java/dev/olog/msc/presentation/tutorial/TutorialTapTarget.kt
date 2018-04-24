package dev.olog.msc.presentation.tutorial

import android.app.Activity
import android.view.View
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.getkeepsafe.taptargetview.TapTargetView
import dev.olog.msc.R

object TutorialTapTarget {

    fun sortBy(text: View, arrow: View){
        val context = text.context

        val textTarget = TapTarget.forView(text, context.getString(R.string.tutorial_sort_by_text))
                .cancelable(false)
                .transparentTarget(true)
                .outerCircleColor(R.color.accent)

        val arrowTarget = TapTarget.forView(arrow, context.getString(R.string.tutorial_sort_by_arrow))
                .cancelable(false)
                .transparentTarget(true)
                .outerCircleColor(R.color.accent)

        TapTargetSequence(text.context as Activity)
                .targets(textTarget, arrowTarget)
                .start()

    }

    fun floatingWindow(view: View){
        val context = view.context

        val target = TapTarget.forView(view, context.getString(R.string.tutorial_floating_window))
                .cancelable(false)
                .transparentTarget(true)
                .outerCircleColor(R.color.accent)
        TapTargetView.showFor(view.context as Activity, target)
    }

    fun lyrics(view: View){
        val context = view.context

        val target = TapTarget.forView(view, context.getString(R.string.tutorial_floating_window))
                .cancelable(false)
                .transparentTarget(true)
                .outerCircleColor(R.color.accent)
        TapTargetView.showFor(view.context as Activity, target)
    }

}