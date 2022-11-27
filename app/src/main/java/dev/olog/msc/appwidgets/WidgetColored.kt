package dev.olog.msc.appwidgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.image.provider.loading.ImageSize
import dev.olog.image.provider.loading.LoadErrorStrategy
import dev.olog.image.provider.loading.Priority
import dev.olog.image.provider.loading.loadImage
import dev.olog.msc.R
import dev.olog.shared.android.palette.ImageProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WidgetColored : BaseWidget() {

    private var job: Job? = null

    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray, remoteViews: RemoteViews?) {
        job?.cancel()
        job = GlobalScope.launch(Dispatchers.Main) {
            val bitmap = context.loadImage(
                mediaId = MediaId.songId(metadata.id),
                loadError = LoadErrorStrategy.Full,
                imageSize = ImageSize.Custom(300),
                priority = Priority.Immediate
            ) ?: return@launch
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