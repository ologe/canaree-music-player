package dev.olog.feature.detail

import android.app.Activity
import android.view.View
import androidx.core.content.ContextCompat
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import dev.olog.shared.extension.findInContext
import dev.olog.ui.extension.tint

object DetailTutorial {

    fun sortBy(text: View, arrow: View){
        val context = text.context

        val textTarget = TapTarget.forView(text, context.getString(R.string.tutorial_sort_by_text))
            .transparentTarget(true)
            .tint(context)

        val arrowTarget = TapTarget.forView(arrow, context.getString(R.string.tutorial_sort_by_arrow))
            .icon(ContextCompat.getDrawable(context, R.drawable.vd_arrow_down))
            .tint(context)

        TapTargetSequence(text.context.findInContext<Activity>())
            .targets(textTarget, arrowTarget)
            .start()

    }

}