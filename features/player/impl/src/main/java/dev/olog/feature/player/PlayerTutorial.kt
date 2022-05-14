package dev.olog.feature.player

import android.app.Activity
import android.view.View
import androidx.core.content.ContextCompat
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import dev.olog.shared.extension.findInContext
import dev.olog.ui.extension.tint

object PlayerTutorial {

    fun lyrics(view: View){
        val context = view.context

        val target = TapTarget.forView(view, context.getString(localization.R.string.tutorial_lyrics))
            .tint(context)
            .icon(ContextCompat.getDrawable(context, dev.olog.ui.R.drawable.vd_offline_lyrics))

        TapTargetView.showFor(view.context.findInContext<Activity>(), target)
    }

}