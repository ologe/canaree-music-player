package dev.olog.msc.presentation.app.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import dev.olog.msc.R
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.utils.images.ImageProcessor
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.getBitmap

private const val IMAGE_SIZE = 300

class WidgetColored : BaseWidget() {

    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray) {

        val model = metadata.toDisplayableItem()
        context.getBitmap(model, IMAGE_SIZE, {
            val remoteViews = RemoteViews(context.packageName, layoutId)
            remoteViews.setTextViewText(R.id.title, metadata.title)
            remoteViews.setTextViewText(R.id.subtitle, DisplayableItem.adjustArtist(metadata.subtitle))

            colorize(context, remoteViews, it)

            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)
        })
    }

    private fun colorize(context: Context, remoteViews: RemoteViews, bitmap: Bitmap){
        val result = ImageProcessor(context).processImage(bitmap)
        remoteViews.setImageViewBitmap(R.id.cover, result.bitmap)

        updateTextColor(remoteViews, result)

        remoteViews.setInt(R.id.background, "setBackgroundColor", result.background)

        setMediaButtonColors(remoteViews, result.primaryTextColor)
    }

    override val layoutId : Int = R.layout.widget_colored

    private fun WidgetMetadata.toDisplayableItem(): DisplayableItem {
        return DisplayableItem(
                0, MediaId.songId(this.id), "", image = this.image
        )
    }

}