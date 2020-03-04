package dev.olog.msc.appwidgets

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import dev.olog.core.MediaId
import dev.olog.image.provider.getCachedBitmap
import dev.olog.msc.R
import dev.olog.shared.android.palette.ImageProcessor
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.*

private const val IMAGE_SIZE = 300

// TODO cancel scope
open class WidgetColored : BaseWidget(), CoroutineScope by MainScope() {

    private var job by autoDisposeJob()

    @SuppressLint("ConcreteDispatcherIssue")
    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray, remoteViews: RemoteViews?) {
        job = launch(Dispatchers.Main) {
            val bitmap = withContext(Dispatchers.IO){
                context.getCachedBitmap(MediaId.songId(metadata.id), IMAGE_SIZE)
            } ?: return@launch
            yield()
            val remote = remoteViews ?: RemoteViews(context.packageName, layoutId)
            remote.setTextViewText(R.id.title, metadata.title)
            remote.setTextViewText(R.id.subtitle, metadata.subtitle)

            colorize(context, remote, bitmap)

            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remote)
        }
    }

    private fun colorize(context: Context, remoteViews: RemoteViews, bitmap: Bitmap){
        val result = ImageProcessor(context).processImage(bitmap)
        remoteViews.setImageViewBitmap(R.id.cover, result.bitmap)

        updateTextColor(remoteViews, result)

        remoteViews.setInt(R.id.background, "setBackgroundColor", result.background)

        setMediaButtonColors(remoteViews, result.primaryTextColor)
    }

    override val layoutId : Int = R.layout.widget_colored
}