package dev.olog.feature.detail.detail

import android.view.View
import androidx.core.content.ContextCompat
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import dev.olog.feature.detail.R
import dev.olog.shared.android.extensions.findActivity
import dev.olog.shared.widgets.extension.tint

object DetailTutorial {

    fun sortBy(text: View, arrow: View){
        val context = text.context

        val textTarget = TapTarget.forView(text, context.getString(R.string.tutorial_sort_by_text))
            .transparentTarget(true)
            .tint(context)

        val arrowTarget = TapTarget.forView(arrow, context.getString(R.string.tutorial_sort_by_arrow))
            .icon(ContextCompat.getDrawable(context, R.drawable.vd_arrow_down))
            .tint(context)

        TapTargetSequence(text.findActivity())
            .targets(textTarget, arrowTarget)
            .start()

    }

}