package dev.olog.msc.presentation.app.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.support.v7.graphics.Palette
import android.widget.RemoteViews
import dev.olog.msc.R
import dev.olog.msc.utils.k.extension.unsubscribe
import dev.olog.shared_android.CoverUtils
import dev.olog.shared_android.ImageUtils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class WidgetClassic : BaseWidget() {

    companion object {
        private var paletteDisposable : Disposable? = null
        private const val IMAGE_SIZE = 300
    }

    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray) {
        val remoteViews = RemoteViews(context.packageName, layoutId)
        remoteViews.setTextViewText(R.id.title, metadata.title)
        remoteViews.setTextViewText(R.id.subtitle, metadata.subtitle)
//        remoteViews.setTextViewText(R.id.duration, " " + TextUtils.MIDDLE_DOT_SPACED + TextUtils.getReadableSongLength(metadata.duration))

        val bitmap = ImageUtils.getBitmapFromUriWithPlaceholder(context,
                Uri.parse(metadata.image) ,metadata.id, IMAGE_SIZE, IMAGE_SIZE)

        remoteViews.setImageViewBitmap(R.id.cover, bitmap)

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)

        colorize(context, remoteViews, appWidgetIds, {
            generatePalette(context, metadata)
        })
    }

    override fun initializeColors(context: Context, remoteViews: RemoteViews, appWidgetIds: IntArray) {
        colorize(context, remoteViews, appWidgetIds, {
            val bitmap = ImageUtils.getBitmapFromDrawable(CoverUtils.getGradient(context, Random().nextInt()))
            remoteViews.setImageViewBitmap(R.id.cover, bitmap)
            generatePalette(bitmap)
        })
    }

    private fun colorize(context: Context, remoteViews: RemoteViews, appWidgetIds: IntArray, func: () -> Palette){
        paletteDisposable.unsubscribe()
        paletteDisposable = Single.fromCallable { func() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    val color = it.getVibrantColor(it.getDarkVibrantColor(Color.BLACK))

                    setMediaButtonColors(remoteViews, color)

                    AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)

                }, Throwable::printStackTrace)
    }

    override val layoutId: Int = R.layout.widget_classic
}