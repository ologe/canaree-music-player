package dev.olog.msc.appwidgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import dev.olog.intents.WidgetConstants

abstract class AbsWidgetApp : AppWidgetProvider() {

    companion object {
        private var metadata : WidgetMetadata? = null
        private var state : WidgetState? = null
        private var actions: WidgetActions? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action){
            WidgetConstants.METADATA_CHANGED -> {

                val appWidgetIds = intent.extras?.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                if (appWidgetIds?.isNotEmpty() == true){

                    val id = intent.getLongExtra(WidgetConstants.ARGUMENT_SONG_ID, 0)
                    val title = intent.getStringExtra(WidgetConstants.ARGUMENT_TITLE)!!
                    val subtitle = intent.getStringExtra(WidgetConstants.ARGUMENT_SUBTITLE)!!
                    metadata =
                        WidgetMetadata(id, title, subtitle)
                    onMetadataChanged(context, metadata!!, appWidgetIds)
                }
            }
            WidgetConstants.STATE_CHANGED -> {
                val appWidgetIds = intent.extras?.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                if (appWidgetIds?.isNotEmpty() == true){
                    val isPlaying = intent.getBooleanExtra(WidgetConstants.ARGUMENT_IS_PLAYING, false)
                    state =
                        WidgetState(isPlaying)
                    onPlaybackStateChanged(context, state!!, appWidgetIds)
                }
            }
            WidgetConstants.ACTION_CHANGED -> {
                val appWidgetIds = intent.extras?.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                if (appWidgetIds?.isNotEmpty() == true){
                    val showPrevious = intent.getBooleanExtra(WidgetConstants.ARGUMENT_SHOW_PREVIOUS, true)
                    val showNext = intent.getBooleanExtra(WidgetConstants.ARGUMENT_SHOW_NEXT, true)
                    actions =
                        WidgetActions(showPrevious, showNext)
                    onActionVisibilityChanged(context, actions!!, appWidgetIds)
                }
            }
            WidgetConstants.QUEUE_CHANGED -> {
                val appWidgetIds = intent.extras?.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                if (appWidgetIds?.isNotEmpty() == true) {
                    onQueueChanged(context, appWidgetIds)
                }
            }
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                // when a new widget is added
                metadata?.let {
                    val appWidgetIds = intent.extras?.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                    if (appWidgetIds != null && appWidgetIds.isNotEmpty()){
                        onMetadataChanged(context, it, appWidgetIds)
                    }
                }
                state?.let {
                    val appWidgetIds = intent.extras?.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                    if (appWidgetIds != null && appWidgetIds.isNotEmpty()){
                        onPlaybackStateChanged(context, it, appWidgetIds)
                    }
                }
                actions?.let {
                    val appWidgetIds = intent.extras?.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                    if (appWidgetIds != null && appWidgetIds.isNotEmpty()){
                        onActionVisibilityChanged(context, it, appWidgetIds)
                    }
                }
            }
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        setupQueue(context, appWidgetIds)
    }

    protected abstract fun onActionVisibilityChanged(context: Context, actions: WidgetActions, appWidgetIds: IntArray)

    protected abstract fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray, remoteViews: RemoteViews? = null)

    protected abstract fun onPlaybackStateChanged(context: Context, state: WidgetState, appWidgetIds: IntArray)

    protected open fun setupQueue(context: Context, appWidgetIds: IntArray) {}
    protected open fun onQueueChanged(context: Context, appWidgetIds: IntArray){}

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        val minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
        val minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        val maxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)

        val newSize = WidgetSize(minWidth, maxWidth, minHeight, maxHeight)

        onSizeChanged(context, appWidgetManager, appWidgetId, newSize)
    }

    /**
     * height:  tiles   min max
     *          1 tile  58  100
     *          2 tile  133 216
     *          3 tile  208  332
     *
     * width:  tiles   min max
     *         4 tile  395 612
     *         3 tile  313 486
     *         2 tile  58  100
     */
    protected abstract fun onSizeChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, size: WidgetSize)

}

