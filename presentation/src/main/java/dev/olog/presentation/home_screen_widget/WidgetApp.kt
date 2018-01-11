package dev.olog.presentation.home_screen_widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.support.v4.graphics.ColorUtils
import android.support.v7.graphics.Palette
import android.widget.RemoteViews
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.presentation.GlideApp
import dev.olog.presentation.R
import dev.olog.presentation.utils.images.PaletteUtil
import dev.olog.shared.unsubscribe
import dev.olog.shared_android.CoverUtils
import dev.olog.shared_android.ImageUtils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WidgetApp : BaseWidgetApp() {

    private var paletteDisposable : Disposable? = null

    override fun onMetadataChanged(context: Context, metadata: Metadata, appWidgetIds: IntArray) {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_app)
        remoteViews.setTextViewText(R.id.title, metadata.title)
        remoteViews.setTextViewText(R.id.subtitle, metadata.subtitle)

        val image = Uri.parse(metadata.image)

        GlideApp.with(context)
                .asBitmap()
                .load(image)
                .placeholder(CoverUtils.getGradient(context, metadata.id.toInt()))
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        remoteViews.setImageViewBitmap(R.id.cover, resource)
                    }
                })

        val bitmap = ImageUtils.getBitmapFromUriWithPlaceholder(context, image, metadata.id)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false)

        paletteDisposable.unsubscribe()
        paletteDisposable = Single.fromCallable { getPalette(scaledBitmap) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    remoteViews.setTextColor(R.id.title, it.primaryText)
                    remoteViews.setTextColor(R.id.subtitle, it.secondaryText)

                    val hGradient = context.getDrawable(R.drawable.horizontal_gradient).mutate() as GradientDrawable
                    hGradient.colors = intArrayOf(
                            it.background,
                            ColorUtils.setAlphaComponent(it.background, 0xa0),
                            ColorUtils.setAlphaComponent(it.background, 22)
                    )

                    remoteViews.setImageViewBitmap(R.id.alphaBackground,
                            ImageUtils.getBitmapFromDrawable(hGradient))

                }, Throwable::printStackTrace)

    }

    private fun getPalette(bitmap: Bitmap): PaletteUtil.ColorsPalette {
        val palette = Palette.from(bitmap)
                .clearFilters()
                .generate()

        val background = palette.getDominantColor(
                palette.getMutedColor(palette.getLightMutedColor(Color.BLACK)))
        val foreground = palette.getLightVibrantColor(palette.getVibrantColor(palette.getDarkVibrantColor(
                palette.getLightMutedColor(Color.BLACK))))

        return PaletteUtil.ensureColors(background, foreground)
    }

    override fun onPlaybackStateChanged(context: Context, isPlaying: Boolean, appWidgetIds: IntArray) {

    }
}