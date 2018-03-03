package dev.olog.msc.music.service.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.graphics.Typeface
import android.support.annotation.CallSuper
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.SpannableString
import android.text.style.StyleSpan
import dagger.Lazy
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.constants.FloatingWindowsConstants
import dev.olog.msc.music.service.model.MediaEntity
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.getBitmap
import javax.inject.Inject
import kotlin.system.measureTimeMillis

open class NotificationImpl21 @Inject constructor(
        protected val service: Service,
        private val token: MediaSessionCompat.Token,
        protected val notificationManager: Lazy<NotificationManager>

) : INotification {

    protected var builder = NotificationCompat.Builder(service, INotification.CHANNEL_ID)

    private var isCreated = false

    override fun createIfNeeded() {
        if (isCreated){
            return
        }

        val mediaStyle = android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(token)
                .setShowActionsInCompactView(1, 2, 3)

        builder.setSmallIcon(R.drawable.vd_bird_not_singing)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(ContextCompat.getColor(service, R.color.dark_grey))
                .setColorized(false)
                .setContentIntent(buildContentIntent())
                .setDeleteIntent(buildPendingIntent(PlaybackStateCompat.ACTION_STOP))
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(mediaStyle)
                .addAction(R.drawable.vd_bird_singing_24dp, "floating info", buildFloatingInfoPendingIntent())
                .addAction(R.drawable.vd_skip_previous, "Previous", buildPendingIntent(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
                .addAction(R.drawable.vd_pause_big, "PlayPause", buildPendingIntent(PlaybackStateCompat.ACTION_PLAY_PAUSE))
                .addAction(R.drawable.vd_skip_next, "Next", buildPendingIntent(PlaybackStateCompat.ACTION_SKIP_TO_NEXT))

        extendInitialization()

        isCreated = true
    }

    protected open fun extendInitialization(){}

    override fun updateState(playbackState: PlaybackStateCompat) {
        val state = playbackState.state
        val isPlaying = state == PlaybackStateCompat.STATE_PLAYING

        val action = builder.mActions[2]
        action.actionIntent = buildPendingIntent(PlaybackStateCompat.ACTION_PLAY_PAUSE)
        action.icon = if (isPlaying) R.drawable.vd_pause_big else R.drawable.vd_play_big
        builder.setSmallIcon(if (isPlaying) R.drawable.vd_bird_singing else R.drawable.vd_bird_not_singing)
        builder.setOngoing(isPlaying)

        if (isPlaying) {
            startChronometer(playbackState)
        } else {
            stopChronometer(playbackState)
        }
    }

    @CallSuper
    protected open fun startChronometer(playbackState: PlaybackStateCompat){
        builder.setWhen(System.currentTimeMillis() - playbackState.position)
                .setShowWhen(true)
                .setUsesChronometer(true)
    }

    @CallSuper
    protected open fun stopChronometer(playbackState: PlaybackStateCompat){
        builder.setWhen(0)
                .setShowWhen(false)
                .setUsesChronometer(false)
    }

    @CallSuper
    override fun updateMetadata(metadata: MediaEntity) {
        val title = metadata.title
        val artist = DisplayableItem.adjustArtist(metadata.artist)
        val album = DisplayableItem.adjustAlbum(metadata.album)

        val spannableTitle = SpannableString(title)
        spannableTitle.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, 0)

        updateMetadataImpl(metadata.id, spannableTitle, artist, album, metadata.image)
    }

    protected open fun updateMetadataImpl (
            id: Long,
            title: SpannableString,
            artist: String,
            album: String,
            image: String){

        println(measureTimeMillis {
            val placeholder = CoverUtils.getGradient(service, id.toInt())
            service.getBitmap(image, placeholder, INotification.IMAGE_SIZE, {
                builder.setLargeIcon(it)
                        .setContentTitle(title)
                        .setContentText(artist)
                        .setSubText(album)
            })
        })
    }

    private fun buildFloatingInfoPendingIntent(): PendingIntent {
        val intent = Intent(service, MainActivity::class.java)
        intent.action = FloatingWindowsConstants.ACTION_START_SERVICE
        return PendingIntent.getActivity(service, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildContentIntent(): PendingIntent {
        val intent = Intent(service, MainActivity::class.java)
        intent.action = AppConstants.ACTION_CONTENT_VIEW
        return PendingIntent.getActivity(service, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildPendingIntent(action: Long): PendingIntent? {
        return MediaButtonReceiver.buildMediaButtonPendingIntent(
                service, ComponentName(service, MediaButtonReceiver::class.java), action)
    }

    override fun update(): android.app.Notification {
        val notification = builder.build()
        notificationManager.get().notify(INotification.NOTIFICATION_ID, notification)
        return notification
    }

    override fun cancel() {
        notificationManager.get().cancel(INotification.NOTIFICATION_ID)
    }
}