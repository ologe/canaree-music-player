package dev.olog.feature.widget.impl.navigation

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.widget.api.FeatureWidgetNavigator
import dev.olog.feature.widget.impl.WidgetColored
import javax.inject.Inject

class FeatureWidgetNavigatorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : FeatureWidgetNavigator {

    private val widgetClasses = listOf(
        WidgetColored::class.java,
    )

    override fun updateMetadata(audioId: Long, title: String, artist: String) {
        for (widgetClass in widgetClasses) {
            val ids = context.getAppWidgetsIdsFor(widgetClass)

            val intent = Intent(context, widgetClass).apply {
                action = WidgetConstants.METADATA_CHANGED
                putExtra(WidgetConstants.ARGUMENT_SONG_ID, audioId)
                putExtra(WidgetConstants.ARGUMENT_TITLE, title)
                putExtra(WidgetConstants.ARGUMENT_SUBTITLE, artist)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }

            context.sendBroadcast(intent)
        }
    }

    override fun updateState(isPlaying: Boolean, bookmark: Long) {
        for (widgetClass in widgetClasses) {
            val ids = context.getAppWidgetsIdsFor(widgetClass)

            val intent = Intent(context, widgetClass).apply {
                action = WidgetConstants.STATE_CHANGED
                putExtra(WidgetConstants.ARGUMENT_IS_PLAYING, isPlaying)
                putExtra(WidgetConstants.ARGUMENT_BOOKMARK, bookmark)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }

            context.sendBroadcast(intent)
        }
    }

    override fun updateActions(showPrevious: Boolean, showNext: Boolean) {
        for (widgetClass in widgetClasses) {
            val ids = context.getAppWidgetsIdsFor(widgetClass)

            val intent = Intent(context, widgetClass).apply {
                action = WidgetConstants.ACTION_CHANGED
                putExtra(WidgetConstants.ARGUMENT_SHOW_PREVIOUS, showPrevious)
                putExtra(WidgetConstants.ARGUMENT_SHOW_NEXT, showNext)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }

            context.sendBroadcast(intent)
        }
    }

    private fun Context.getAppWidgetsIdsFor(clazz: Class<*>): IntArray {
        return AppWidgetManager.getInstance(this).getAppWidgetIds(ComponentName(this, clazz))
    }

}