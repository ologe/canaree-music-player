package dev.olog.feature.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.ApplicationScope
import dev.olog.core.MediaId
import dev.olog.image.provider.getCachedBitmap
import dev.olog.ui.palette.ImageProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import javax.inject.Inject

private const val IMAGE_SIZE = 300

// todo rewrite in glance
@AndroidEntryPoint
class WidgetColored : BaseWidget() {

    @Inject
    lateinit var applicationScope: ApplicationScope

    private var job: Job? = null

    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray, remoteViews: RemoteViews?) {
        job?.cancel()
        job = applicationScope.launch {
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