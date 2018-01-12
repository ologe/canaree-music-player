package dev.olog.presentation.widget_home_screen

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.SystemClock
import android.support.v4.content.ContextCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.graphics.Palette
import android.view.View
import android.widget.RemoteViews
import dev.olog.presentation.R
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.utils.images.ImageProcessorResult
import dev.olog.shared.constants.FloatingInfoConstants
import dev.olog.shared.constants.MusicConstants
import dev.olog.shared_android.ImageUtils
import dev.olog.shared_android.interfaces.MusicServiceClass
import javax.inject.Inject

abstract class BaseWidget : AbsWidgetApp() {

    companion object {
        private var IS_PLAYING = false
    }

    @Inject lateinit var musicServiceClass: MusicServiceClass

    override fun onPlaybackStateChanged(context: Context, state: WidgetState, appWidgetIds: IntArray) {
        BaseWidget.IS_PLAYING = state.isPlaying

        val remoteViews = RemoteViews(context.packageName, layoutId)

        val playPauseIcon = if (state.isPlaying){
            ContextCompat.getDrawable(context, R.drawable.vd_pause_big)!!
        } else ContextCompat.getDrawable(context, R.drawable.vd_play_big)!!

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

    protected fun generatePalette(context: Context, metadata: WidgetMetadata): Palette {
        val uri = Uri.parse(metadata.image)

        val bitmap = ImageUtils.getBitmapFromUriWithPlaceholder(context, uri, metadata.id)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false)

        return Palette.from(scaledBitmap).generate()
    }

    protected fun setMediaButtonColors(remoteViews: RemoteViews, color: Int){
        remoteViews.setInt(R.id.floatingWindow, "setColorFilter", color)
        remoteViews.setInt(R.id.favorite, "setColorFilter", color)
        remoteViews.setInt(R.id.previous, "setColorFilter", color)
        remoteViews.setInt(R.id.play, "setColorFilter", color)
        remoteViews.setInt(R.id.next, "setColorFilter", color)
    }

    protected fun updateTextColor(remoteViews: RemoteViews, palette: ImageProcessorResult){
        remoteViews.setTextColor(R.id.title, palette.primaryTextColor)
        remoteViews.setTextColor(R.id.subtitle, palette.secondaryTextColor)
        remoteViews.setTextColor(R.id.bookmark, palette.secondaryTextColor)
        remoteViews.setTextColor(R.id.duration, palette.secondaryTextColor)
    }

    protected abstract val layoutId : Int

}