package dev.olog.presentation.widget_home_screen

import android.appwidget.AppWidgetManager
import android.content.Context
import android.net.Uri
import android.widget.RemoteViews
import dev.olog.presentation.R
import dev.olog.presentation.utils.images.ImageProcessor
import dev.olog.shared_android.ImageUtils
import dev.olog.shared_android.TextUtils

class WidgetColored : BaseWidget() {

    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray) {
        val remoteViews = RemoteViews(context.packageName, layoutId)
        remoteViews.setTextViewText(R.id.title, metadata.title)
        remoteViews.setTextViewText(R.id.subtitle, metadata.subtitle)
        remoteViews.setTextViewText(R.id.duration, " " + TextUtils.MIDDLE_DOT_SPACED + TextUtils.getReadableSongLength(metadata.duration))

        val bitmap = ImageUtils.getBitmapFromUriWithPlaceholder(context,
                Uri.parse(metadata.image), metadata.id)

        val result = ImageProcessor(context).processImage(bitmap)
        remoteViews.setImageViewBitmap(R.id.cover, result.bitmap)

        updateTextColor(remoteViews, result)

        remoteViews.setInt(R.id.background, "setBackgroundColor", result.background)

        setMediaButtonColors(remoteViews, result.primaryTextColor)

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)
    }

    override val layoutId: Int = R.layout.widget_colored

}