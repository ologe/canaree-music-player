package dev.olog.msc.presentation.tutorial

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.taptargetview.TapTarget
import dev.olog.msc.taptargetview.TapTargetSequence
import dev.olog.msc.taptargetview.TapTargetView
import dev.olog.msc.utils.k.extension.colorAccent
import dev.olog.msc.utils.k.extension.windowBackground

object TutorialTapTarget {

    fun sortBy(text: View, arrow: View){
        val context = text.context

        val textTarget = TapTarget.forView(text, context.getString(R.string.tutorial_sort_by_text))
                .transparentTarget(true)
                .tint(context)

        val arrowTarget = TapTarget.forView(arrow, context.getString(R.string.tutorial_sort_by_arrow))
                .transparentTarget(true)
                .tint(context)

        TapTargetSequence(text.context as Activity)
                .targets(textTarget, arrowTarget)
                .start()

    }

    fun floatingWindow(view: View){
        val context = view.context

        val target = TapTarget.forView(view, context.getString(R.string.tutorial_floating_window))
                .transparentTarget(true)
                .tint(context)
        TapTargetView.showFor(view.context as Activity, target)
    }

    fun lyrics(view: View){
        val context = view.context

        val target = TapTarget.forView(view, context.getString(R.string.tutorial_lyrics))
                .tint(context)
                .icon(ContextCompat.getDrawable(context, R.drawable.vd_offline_lyrics))

        TapTargetView.showFor(view.context as Activity, target)
    }

    fun addLyrics(search: View, edit: View, sync: View){
        val context = search.context

        val searchTarget = TapTarget.forView(search, context.getString(R.string.tutorial_search_lyrics))
                .tint(context)
                .icon(ContextCompat.getDrawable(context, R.drawable.vd_search))

        val editTarget = TapTarget.forView(edit, context.getString(R.string.tutorial_add_lyrics))
                .tint(context)
                .icon(ContextCompat.getDrawable(context, R.drawable.vd_edit))

        val syncLyrics = TapTarget.forView(sync, context.getString(R.string.tutorial_adjust_sync))
                .tint(context)
                .icon(ContextCompat.getDrawable(context, R.drawable.vd_sync))

        TapTargetSequence(search.context as Activity)
                .targets(editTarget, searchTarget, syncLyrics)
                .start()
    }

    private fun TapTarget.tint(context: Context): TapTarget {
        val accentColor = context.colorAccent()
        val backgroundColor = context.windowBackground()
        val dimColor = if (AppTheme.isWhiteTheme()) Color.WHITE else Color.BLACK

        return this.tintTarget(true)
                .outerCircleColorInt(accentColor)
                .targetCircleColorInt(backgroundColor)
//                .dimColorInt(dimColor)
    }

}