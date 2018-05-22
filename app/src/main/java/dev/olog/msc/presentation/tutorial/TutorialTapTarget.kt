package dev.olog.msc.presentation.tutorial

import android.app.Activity
import android.support.v4.content.ContextCompat
import android.view.View
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.getkeepsafe.taptargetview.TapTargetView
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.AppTheme

object TutorialTapTarget {

    fun sortBy(text: View, arrow: View){
        val context = text.context

        val textTarget = TapTarget.forView(text, context.getString(R.string.tutorial_sort_by_text))
                .transparentTarget(true)
                .tint()

        val arrowTarget = TapTarget.forView(arrow, context.getString(R.string.tutorial_sort_by_arrow))
                .transparentTarget(true)
                .tint()

        TapTargetSequence(text.context as Activity)
                .targets(textTarget, arrowTarget)
                .start()

    }

    fun floatingWindow(view: View){
        val context = view.context

        val target = TapTarget.forView(view, context.getString(R.string.tutorial_floating_window))
                .transparentTarget(true)
                .tint()
        TapTargetView.showFor(view.context as Activity, target)
    }

    fun lyrics(view: View){
        val context = view.context

        val target = TapTarget.forView(view, context.getString(R.string.tutorial_lyrics))
                .tint()
                .icon(ContextCompat.getDrawable(context, R.drawable.vd_offline_lyrics))

        TapTargetView.showFor(view.context as Activity, target)
    }

    fun addLyrics(search: View, edit: View, sync: View){
        val context = search.context

        val searchTarget = TapTarget.forView(search, context.getString(R.string.tutorial_search_lyrics))
                .tint()
                .icon(ContextCompat.getDrawable(context, R.drawable.vd_search))

        val editTarget = TapTarget.forView(edit, context.getString(R.string.tutorial_add_lyrics))
                .tint()
                .icon(ContextCompat.getDrawable(context, R.drawable.vd_edit))

        val syncLyrics = TapTarget.forView(sync, context.getString(R.string.tutorial_adjust_sync))
                .tint()
                .icon(ContextCompat.getDrawable(context, R.drawable.vd_sync))

        TapTargetSequence(search.context as Activity)
                .targets(editTarget, searchTarget, syncLyrics)
                .start()
    }

    private fun TapTarget.tint(): TapTarget {
        val accentColor = if (AppTheme.isDarkTheme()) R.color.accent_secondary else R.color.accent
        val backgroundColor = when {
            AppTheme.isDarkMode() -> R.color.theme_dark_background
            AppTheme.isBlackMode() -> R.color.theme_black_background
            else -> R.color.background
        }

        return this.tintTarget(true)
                .outerCircleColor(accentColor)
                .targetCircleColor(backgroundColor)
    }

}