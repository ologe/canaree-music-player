package dev.olog.presentation.widget_home_screen

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.RemoteViews
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
import dev.olog.shared_android.interfaces.MusicServiceClass
import javax.inject.Inject

abstract class BaseWidget : AbsWidgetApp() {

    @Inject lateinit var musicServiceClass: MusicServiceClass
    protected var isPlaying = false

    override fun onMetadataChanged(context: Context, metadata: WidgetMetadata, appWidgetIds: IntArray) {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_colored)
        remoteViews.setTextViewText(R.id.title, metadata.title)
        remoteViews.setTextViewText(R.id.subtitle, metadata.subtitle)

        GlideApp.with(context)
                .asBitmap()
                .load(metadata.image)
                .placeholder(CoverUtils.getGradient(context, metadata.id.toInt()))
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        remoteViews.setImageViewBitmap(R.id.cover, resource)
                        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)
                    }
                })

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)
    }



    override fun onPlaybackStateChanged(context: Context, isPlaying: Boolean, appWidgetIds: IntArray) {
        this.isPlaying = isPlaying

        val remoteViews = RemoteViews(context.packageName, R.layout.widget_colored)

        val playPauseIcon = if (isPlaying){
            ContextCompat.getDrawable(context, R.drawable.vd_play_big)!!
        } else ContextCompat.getDrawable(context, R.drawable.vd_pause_big)!!

        val buttonColor = getLastPalette()?.primaryText ?: Color.BLACK

        playPauseIcon.setColorFilter(buttonColor, PorterDuff.Mode.SRC_ATOP)
        remoteViews.setImageViewBitmap(R.id.play, ImageUtils.getBitmapFromDrawable(playPauseIcon))

        remoteViews.setOnClickPendingIntent(R.id.floatingWindow, buildFloatingInfoPendingIntent(context))
        remoteViews.setOnClickPendingIntent(R.id.favorite, buildUpdateFavoriteIntent(context))
        remoteViews.setOnClickPendingIntent(R.id.previous, buildPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
        remoteViews.setOnClickPendingIntent(R.id.play, buildPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE))
        remoteViews.setOnClickPendingIntent(R.id.next, buildPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))
        remoteViews.setOnClickPendingIntent(R.id.cover, buildContentIntent(context))

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)
    }

    override fun onActionVisibilityChanged(context: Context, showPrevious: Boolean, showNext: Boolean, appWidgetIds: IntArray) {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_colored)

        val previousVisibility = if (showPrevious) View.VISIBLE else View.INVISIBLE
        val nextVisibility = if (showNext) View.VISIBLE else View.INVISIBLE

        val previousPendingIntent = if (showPrevious) buildPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
            else null
        val nextPendingIntent = if (showPrevious) buildPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
            else null

        remoteViews.setViewVisibility(R.id.previous, previousVisibility)
        remoteViews.setViewVisibility(R.id.next, nextVisibility)
        remoteViews.setOnClickPendingIntent(R.id.previous, previousPendingIntent)
        remoteViews.setOnClickPendingIntent(R.id.next, nextPendingIntent)

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews)
    }

    protected open fun getLastPalette(): PaletteUtil.ColorsPalette? = null


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

}