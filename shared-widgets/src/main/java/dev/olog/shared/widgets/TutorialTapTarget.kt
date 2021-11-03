package dev.olog.shared.widgets

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.getkeepsafe.taptargetview.TapTargetView
import dev.olog.shared.android.extensions.colorAccent
import dev.olog.shared.android.extensions.colorBackground
import dev.olog.shared.android.extensions.findInContext

object TutorialTapTarget {

    fun sortBy(text: View, arrow: View){
        val context = text.context

        val textTarget = TapTarget.forView(text, context.getString(localization.R.string.tutorial_sort_by_text))
                .transparentTarget(true)
                .tint(context)

        val arrowTarget = TapTarget.forView(arrow, context.getString(localization.R.string.tutorial_sort_by_arrow))
                .icon(ContextCompat.getDrawable(context, dev.olog.shared.android.R.drawable.vd_arrow_down))
                .tint(context)

        TapTargetSequence(text.context.findInContext<Activity>())
                .targets(textTarget, arrowTarget)
                .start()

    }

    fun floatingWindow(view: View){
        val context = view.context

        val target = TapTarget.forView(view, context.getString(localization.R.string.tutorial_floating_window))
                .icon(ContextCompat.getDrawable(context, dev.olog.shared.android.R.drawable.vd_search_text))
                .tint(context)
        TapTargetView.showFor(view.context.findInContext<Activity>(), target)
    }

    fun lyrics(view: View){
        val context = view.context

        val target = TapTarget.forView(view, context.getString(localization.R.string.tutorial_lyrics))
                .tint(context)
                .icon(ContextCompat.getDrawable(context, dev.olog.shared.android.R.drawable.vd_offline_lyrics))

        TapTargetView.showFor(view.context.findInContext<Activity>(), target)
    }

    fun addLyrics(search: View, edit: View, sync: View){
        val context = search.context

        val searchTarget = TapTarget.forView(search, context.getString(localization.R.string.tutorial_search_lyrics))
                .tint(context)
                .icon(ContextCompat.getDrawable(context, dev.olog.shared.android.R.drawable.vd_search))

        val editTarget = TapTarget.forView(edit, context.getString(localization.R.string.tutorial_add_lyrics))
                .tint(context)
                .icon(ContextCompat.getDrawable(context, dev.olog.shared.android.R.drawable.vd_edit))

        val syncLyrics = TapTarget.forView(sync, context.getString(localization.R.string.tutorial_adjust_sync))
                .tint(context)
                .icon(ContextCompat.getDrawable(context, dev.olog.shared.android.R.drawable.vd_sync))

        TapTargetSequence(search.context.findInContext<Activity>())
                .targets(editTarget, searchTarget, syncLyrics)
                .start()
    }

    private fun TapTarget.tint(context: Context): TapTarget {
        val accentColor = context.colorAccent()
        val backgroundColor = context.colorBackground()

        return this.tintTarget(true)
                .outerCircleColorInt(accentColor)
                .targetCircleColorInt(backgroundColor)
    }

}