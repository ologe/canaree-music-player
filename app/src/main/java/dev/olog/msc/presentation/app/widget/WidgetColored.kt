package dev.olog.msc.presentation.app.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.RemoteViews
import dev.olog.msc.R
import dev.olog.msc.presentation.utils.images.ImageProcessor
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.img.ImageUtils
import java.util.*

private const val IMAGE_SIZE = 300

class WidgetColored : BaseWidget() {

    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray) {
        val remoteViews = RemoteViews(context.packageName, layoutId)
        remoteViews.setTextViewText(R.id.title, metadata.title)
        remoteViews.setTextViewText(R.id.subtitle, metadata.subtitle)

        val bitmap = ImageUtils.getBitmapFromUriWithPlaceholder(context, Uri.parse(metadata.image), metadata.id, IMAGE_SIZE, IMAGE_SIZE)
        colorize(context, remoteViews, bitmap)

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)
    }

    override fun initializeColors(context: Context, remoteViews: RemoteViews, appWidgetIds: IntArray) {
        val bitmap = ImageUtils.getBitmapFromDrawable(CoverUtils.getGradient(context, Random().nextInt()))
        colorize(context, remoteViews, bitmap)
    }

    private fun colorize(context: Context, remoteViews: RemoteViews, bitmap: Bitmap){

        val result = ImageProcessor(context).processImage(bitmap)
        remoteViews.setImageViewBitmap(R.id.cover, result.bitmap)

        updateTextColor(remoteViews, result)

        remoteViews.setInt(R.id.background, "setBackgroundColor", result.background)

        setMediaButtonColors(remoteViews, result.primaryTextColor)
    }

    override val layoutId: Int = R.layout.widget_colored

}