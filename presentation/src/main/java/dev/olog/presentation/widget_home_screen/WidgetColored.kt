package dev.olog.presentation.widget_home_screen

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.RemoteViews
import dev.olog.presentation.R
import dev.olog.presentation.utils.images.ImageProcessor
import dev.olog.shared_android.CoverUtils
import dev.olog.shared_android.ImageUtils
import java.util.*

class WidgetColored : BaseWidget() {

    companion object {
        private const val IMAGE_SIZE = 300
    }

    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray) {
        val remoteViews = RemoteViews(context.packageName, layoutId)
        remoteViews.setTextViewText(R.id.title, metadata.title)
        remoteViews.setTextViewText(R.id.subtitle, metadata.subtitle)
//        remoteViews.setTextViewText(R.id.duration, " " + TextUtils.MIDDLE_DOT_SPACED + TextUtils.getReadableSongLength(metadata.duration))

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