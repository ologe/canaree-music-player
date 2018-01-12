package dev.olog.presentation.widget_home_screen

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v7.graphics.Palette
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


    private var palette: PaletteUtil.ColorsPalette? = null

    private var paletteDisposable : Disposable? = null

    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray) {
        super.onMetadataChanged(context, metadata, appWidgetIds)
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_colored)

        val image = Uri.parse(metadata.image)

        val bitmap = ImageUtils.getBitmapFromUriWithPlaceholder(context, image, metadata.id)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false)

        paletteDisposable.unsubscribe()
        paletteDisposable = Single.fromCallable { generatePalette(scaledBitmap) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    savePalette(it)

                    updateTextColor(remoteViews, it)

                    updateBackgroundColor(context, remoteViews, it)

                    setMediaButtonColors(context, remoteViews, it)

                    AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)

                }, Throwable::printStackTrace)
    }

    private fun savePalette(palette: PaletteUtil.ColorsPalette){
        this.palette = palette
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

    private fun updateTextColor(remoteViews: RemoteViews, palette: PaletteUtil.ColorsPalette){
        remoteViews.setTextColor(R.id.title, palette.primaryText)
        remoteViews.setTextColor(R.id.subtitle, palette.secondaryText)
    }

    private fun setMediaButtonColors(context: Context, remoteViews: RemoteViews, palette: PaletteUtil.ColorsPalette){
        val button = listOf(
                R.id.floatingWindow to ContextCompat.getDrawable(context, R.drawable.vd_bird_singing_24dp)!!,
                R.id.favorite to ContextCompat.getDrawable(context, R.drawable.vd_not_favorite)!!,
                R.id.previous to ContextCompat.getDrawable(context, R.drawable.vd_skip_previous)!!,
                R.id.play to ContextCompat.getDrawable(context,
                        if (isPlaying) R.drawable.vd_play_big else R.drawable.vd_pause_big)!! ,
                R.id.next to ContextCompat.getDrawable(context, R.drawable.vd_skip_next)!!
        )
        for ((id, drawable) in button) {
            drawable.setColorFilter(palette.primaryText, PorterDuff.Mode.SRC_ATOP)
            remoteViews.setImageViewBitmap(id, ImageUtils.getBitmapFromDrawable(drawable))
        }
    }

    private fun generatePalette(bitmap: Bitmap): PaletteUtil.ColorsPalette {
        val palette = Palette.from(bitmap)
                .maximumColorCount(20)
                .generate()

        val background = palette.getDominantColor(palette.getDarkMutedColor(palette.getMutedColor(palette.getLightMutedColor(Color.BLACK))))
        val foreground = palette.getLightVibrantColor(palette.getVibrantColor(palette.getDarkVibrantColor(Color.WHITE)))

        return PaletteUtil.ensureColors(background, foreground)
    }

    override fun getLastPalette(): PaletteUtil.ColorsPalette? = palette

}