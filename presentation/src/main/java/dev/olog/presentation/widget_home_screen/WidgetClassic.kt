package dev.olog.presentation.widget_home_screen

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.widget.RemoteViews
import dev.olog.presentation.R
import dev.olog.shared.unsubscribe
import dev.olog.shared_android.ImageUtils
import dev.olog.shared_android.TextUtils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WidgetClassic : BaseWidget() {

    companion object {
        private var paletteDisposable : Disposable? = null
    }

    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray) {
        val remoteViews = RemoteViews(context.packageName, layoutId)
        remoteViews.setTextViewText(R.id.title, metadata.title)
        remoteViews.setTextViewText(R.id.subtitle, metadata.subtitle)
        remoteViews.setTextViewText(R.id.duration, " " + TextUtils.MIDDLE_DOT_SPACED + TextUtils.getReadableSongLength(metadata.duration))

        val bitmap = ImageUtils.getBitmapFromUriWithPlaceholder(context,
                Uri.parse(metadata.image) ,metadata.id)

        remoteViews.setImageViewBitmap(R.id.cover, bitmap)

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)

        paletteDisposable.unsubscribe()
        paletteDisposable = Single.fromCallable { generatePalette(context, metadata) }
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