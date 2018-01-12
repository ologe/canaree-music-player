package dev.olog.presentation.widget_home_screen

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.SystemClock
import android.support.v4.content.ContextCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.graphics.Palette
import android.view.View
import android.widget.RemoteViews
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.presentation.GlideApp
import dev.olog.presentation.R
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.utils.images.PaletteUtil
import dev.olog.shared.constants.FloatingInfoConstants
import dev.olog.shared.constants.MusicConstants
import dev.olog.shared_android.CoverUtils
import dev.olog.shared_android.ImageUtils
import dev.olog.shared_android.TextUtils
import dev.olog.shared_android.interfaces.MusicServiceClass
import javax.inject.Inject

abstract class BaseWidget : AbsWidgetApp() {

    companion object {
        private var IS_PLAYING = false
    }

    @Inject lateinit var musicServiceClass: MusicServiceClass

    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray) {
        val remoteViews = RemoteViews(context.packageName, layoutId)
        remoteViews.setTextViewText(R.id.title, metadata.title)
        remoteViews.setTextViewText(R.id.subtitle, metadata.subtitle)
        remoteViews.setTextViewText(R.id.duration, TextUtils.MIDDLE_DOT_SPACED + TextUtils.getReadableSongLength(metadata.duration))

        GlideApp.with(context)
                .asBitmap()
                .load(metadata.image)
                .priority(Priority.IMMEDIATE)
                .override(200)
                .placeholder(CoverUtils.getGradient(context, metadata.id.toInt()))
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        remoteViews.setImageViewBitmap(R.id.cover, resource)
                        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)
                    }
                })

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)
    }

    override fun onPlaybackStateChanged(context: Context, state: WidgetState, appWidgetIds: IntArray) {
        BaseWidget.IS_PLAYING = state.isPlaying

        val remoteViews = RemoteViews(context.packageName, layoutId)

        val playPauseIcon = if (state.isPlaying){
            ContextCompat.getDrawable(context, R.drawable.vd_pause_big)!!
        } else ContextCompat.getDrawable(context, R.drawable.vd_play_big)!!

        val buttonColor = getColorPalette()?.primaryText ?: Color.BLACK

        playPauseIcon.setColorFilter(buttonColor, PorterDuff.Mode.SRC_ATOP)
        remoteViews.setImageViewBitmap(R.id.play, ImageUtils.getBitmapFromDrawable(playPauseIcon))

        remoteViews.setOnClickPendingIntent(R.id.floatingWindow, buildFloatingInfoPendingIntent(context))
        remoteViews.setOnClickPendingIntent(R.id.favorite, buildUpdateFavoriteIntent(context))
        remoteViews.setOnClickPendingIntent(R.id.previous, buildPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
        remoteViews.setOnClickPendingIntent(R.id.play, buildPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE))
        remoteViews.setOnClickPendingIntent(R.id.next, buildPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))
        remoteViews.setOnClickPendingIntent(R.id.cover, buildContentIntent(context))

        remoteViews.setChronometer(R.id.bookmark, SystemClock.elapsedRealtime() - state.bookmark,
                null, state.isPlaying)

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)
    }

    override fun onActionVisibilityChanged(context: Context, showPrevious: Boolean, showNext: Boolean, appWidgetIds: IntArray) {
        val remoteViews = RemoteViews(context.packageName, layoutId)

        val previousVisibility = if (showPrevious) View.VISIBLE else View.INVISIBLE
        val nextVisibility = if (showNext) View.VISIBLE else View.INVISIBLE

        val previousPendingIntent = if (showPrevious) buildPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
            else null
        val nextPendingIntent = if (showNext) buildPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
            else null

        remoteViews.setViewVisibility(R.id.previous, previousVisibility)
        remoteViews.setViewVisibility(R.id.next, nextVisibility)
        remoteViews.setOnClickPendingIntent(R.id.previous, previousPendingIntent)
        remoteViews.setOnClickPendingIntent(R.id.next, nextPendingIntent)

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)
    }

    private fun buildPendingIntent(context: Context, action: Long): PendingIntent? {
        return MediaButtonReceiver.buildMediaButtonPendingIntent(
                context, ComponentName(context, MediaButtonReceiver::class.java), action)
    }

    private fun buildFloatingInfoPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = FloatingInfoConstants.ACTION_START_SERVICE
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildUpdateFavoriteIntent(context: Context): PendingIntent {
        val intent = Intent(context, musicServiceClass.get())
        intent.action = MusicConstants.ACTION_TOGGLE_FAVORITE
        return PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildContentIntent(context: Context): PendingIntent {
        return PendingIntent.getActivity(context, 0,
                Intent(context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
    }

    protected fun generatePaletteColors(context: Context, metadata: WidgetMetadata): PaletteUtil.ColorsPalette {
        val palette = generatePalette(context, metadata)

        val background = palette.getDominantColor(palette.getDarkMutedColor(palette.getMutedColor(palette.getLightMutedColor(Color.BLACK))))
        val foreground = palette.getLightVibrantColor(palette.getVibrantColor(palette.getDarkVibrantColor(Color.WHITE)))

        return PaletteUtil.ensureColors(background, foreground)
    }

    protected fun generatePalette(context: Context, metadata: WidgetMetadata): Palette {
        val uri = Uri.parse(metadata.image)

        val bitmap = ImageUtils.getBitmapFromUriWithPlaceholder(context, uri, metadata.id)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false)

        return Palette.from(scaledBitmap)
                .maximumColorCount(20)
                .generate()
    }

    protected fun setMediaButtonColors(context: Context, remoteViews: RemoteViews, palette: PaletteUtil.ColorsPalette){
        val button = listOf(
                R.id.floatingWindow to ContextCompat.getDrawable(context, R.drawable.vd_bird_singing_24dp)!!,
                R.id.favorite to ContextCompat.getDrawable(context, R.drawable.vd_not_favorite)!!,
                R.id.previous to ContextCompat.getDrawable(context, R.drawable.vd_skip_previous)!!,
                R.id.play to ContextCompat.getDrawable(context,
                        if (IS_PLAYING) R.drawable.vd_pause_big else R.drawable.vd_play_big)!! ,
                R.id.next to ContextCompat.getDrawable(context, R.drawable.vd_skip_next)!!
        )
        for ((id, drawable) in button) {
            drawable.setColorFilter(palette.primaryText, PorterDuff.Mode.SRC_ATOP)
            remoteViews.setImageViewBitmap(id, ImageUtils.getBitmapFromDrawable(drawable))
        }
    }

    protected fun updateTextColor(remoteViews: RemoteViews, palette: PaletteUtil.ColorsPalette){
        remoteViews.setTextColor(R.id.title, palette.primaryText)
        remoteViews.setTextColor(R.id.subtitle, palette.secondaryText)
        remoteViews.setTextColor(R.id.bookmark, palette.secondaryText)
        remoteViews.setTextColor(R.id.duration, palette.secondaryText)
    }

    protected open fun getColorPalette() : PaletteUtil.ColorsPalette? = null

    protected abstract val layoutId : Int

}