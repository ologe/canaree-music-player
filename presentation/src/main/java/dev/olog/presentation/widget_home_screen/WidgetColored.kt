package dev.olog.presentation.widget_home_screen

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.v4.graphics.ColorUtils
import android.widget.RemoteViews
import dev.olog.presentation.R
import dev.olog.presentation.utils.images.PaletteUtil
import dev.olog.shared.unsubscribe
import dev.olog.shared_android.ImageUtils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WidgetColored : BaseWidget() {

    companion object {
        private var paletteDisposable : Disposable? = null
        private var palette: PaletteUtil.ColorsPalette? = null
    }

    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray) {

        paletteDisposable.unsubscribe()
        paletteDisposable = Single.fromCallable { generatePaletteColors(context, metadata) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val remoteViews = RemoteViews(context.packageName, layoutId)

                    savePalette(it)

                    updateTextColor(remoteViews, it)

                    updateBackgroundColor(context, remoteViews, it)

                    setMediaButtonColors(context, remoteViews, it)

                    super.onMetadataChanged(context, metadata, appWidgetIds)
//                    AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)

                }, Throwable::printStackTrace)
    }


    private fun updateBackgroundColor(context: Context, remoteViews: RemoteViews, palette: PaletteUtil.ColorsPalette){
        val hGradient = context.getDrawable(R.drawable.horizontal_gradient).mutate() as GradientDrawable
        hGradient.colors = intArrayOf(
                palette.background,
                ColorUtils.setAlphaComponent(palette.background, 0xa0),
                ColorUtils.setAlphaComponent(palette.background, 22)
        )

        remoteViews.setImageViewBitmap(R.id.alphaBackground,
                ImageUtils.getBitmapFromDrawable(hGradient))

        remoteViews.setInt(R.id.background, "setBackgroundColor", palette.background)
    }

    private fun savePalette(palette: PaletteUtil.ColorsPalette){
        WidgetColored.palette = palette
    }

    override fun getColorPalette() = palette

    override val layoutId: Int = R.layout.widget_colored

}