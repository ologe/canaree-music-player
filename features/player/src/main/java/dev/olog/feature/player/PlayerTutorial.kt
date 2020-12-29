package dev.olog.feature.player

import android.view.View
import androidx.core.content.ContextCompat
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import dev.olog.shared.android.extensions.findActivity
import dev.olog.shared.widgets.extension.tint

object PlayerTutorial {

    fun lyrics(view: View){
        val context = view.context

        val target = TapTarget.forView(view, context.getString(R.string.tutorial_lyrics))
            .tint(context)
            .icon(ContextCompat.getDrawable(context, R.drawable.vd_offline_lyrics))

        TapTargetView.showFor(view.findActivity(), target)
    }

}