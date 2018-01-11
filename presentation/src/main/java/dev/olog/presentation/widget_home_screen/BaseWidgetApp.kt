package dev.olog.presentation.widget_home_screen

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import dev.olog.shared_android.WidgetConstants

abstract class BaseWidgetApp : AppWidgetProvider() {


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action){
            WidgetConstants.METADATA_CHANGED -> {

                val appWidgetIds = intent.extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                if (appWidgetIds != null && appWidgetIds.isNotEmpty()){

                    val id = intent.getLongExtra(WidgetConstants.ARGUMENT_SONG_ID, 0)
                    val title = intent.getStringExtra(WidgetConstants.ARGUMENT_TITLE)
                    val subtitle = intent.getStringExtra(WidgetConstants.ARGUMENT_SUBTITLE)
                    val image = intent.getStringExtra(WidgetConstants.ARGUMENT_IMAGE)
                    val metadata = WidgetMetadata(id, title, subtitle, image)
                    onMetadataChanged(context, metadata, appWidgetIds)
                }
            }
            WidgetConstants.STATE_CHANGED -> {
                val appWidgetIds = intent.extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                if (appWidgetIds != null && appWidgetIds.isNotEmpty()){
                    val isPlaying = intent.getBooleanExtra(WidgetConstants.ARGUMENT_IS_PLAYING, false)
                    onPlaybackStateChanged(context, isPlaying, appWidgetIds)
                }
            }
        }
    }



    protected abstract fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray)

    protected abstract fun onPlaybackStateChanged(context: Context, isPlaying: Boolean, appWidgetIds: IntArray)

    data class WidgetMetadata(
            val id: Long,
            val title: String,
            val subtitle: String,
            val image: String
    )

}