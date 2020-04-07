package dev.olog.msc.appwidgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import dev.olog.shared.coroutines.MainScope
import dev.olog.shared.coroutines.autoDisposeJob
import dev.olog.domain.MediaId.Companion.SONGS_CATEGORY
import dev.olog.domain.schedulers.Schedulers
import dev.olog.image.provider.getCachedBitmap
import dev.olog.msc.R
import dev.olog.shared.android.palette.ImageProcessor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import javax.inject.Inject

private const val IMAGE_SIZE = 300

class WidgetColored : BaseWidget() {

    // TODO cancel scope
    private val scope by MainScope()
    private var job by autoDisposeJob()

    @Inject
    internal lateinit var schedulers: Schedulers

    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray, remoteViews: RemoteViews?) {
        job = scope.launch {
            val bitmap = withContext(schedulers.io){
                context.getCachedBitmap(SONGS_CATEGORY.playableItem(metadata.id), IMAGE_SIZE)
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