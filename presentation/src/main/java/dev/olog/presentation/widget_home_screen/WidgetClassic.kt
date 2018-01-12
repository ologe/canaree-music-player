package dev.olog.presentation.widget_home_screen

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Color
import android.widget.RemoteViews
import dev.olog.presentation.R
import dev.olog.presentation.utils.images.PaletteUtil
import dev.olog.shared.unsubscribe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WidgetClassic : BaseWidget() {

    companion object {
        private var paletteDisposable : Disposable? = null
        private var palette: PaletteUtil.ColorsPalette? = null
    }

    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray) {
        super.onMetadataChanged(context, metadata, appWidgetIds)

        paletteDisposable.unsubscribe()
        paletteDisposable = Single.fromCallable { generatePalette(context, metadata) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val remoteViews = RemoteViews(context.packageName, layoutId)

                    val color = it.getLightVibrantColor(it.getVibrantColor(it.getDarkVibrantColor(Color.BLACK)))

                    setMediaButtonColors(context, remoteViews,
                            PaletteUtil.ColorsPalette(0, color, 0))

                    AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)

                }, Throwable::printStackTrace)
    }

    override fun getColorPalette() = palette

    override val layoutId: Int = R.layout.widget_classic
}