package dev.olog.music_service.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.support.annotation.CallSuper
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Pair
import dagger.Lazy
import dev.olog.music_service.ActivityClass
import dev.olog.music_service.R
import dev.olog.music_service.di.PerService
import dev.olog.music_service.model.MediaEntity
import dev.olog.music_service.utils.ImageUtils
import javax.inject.Inject

@PerService
open class NotificationImpl21 @Inject constructor(
        protected val service: Service,
        private val activityClass: ActivityClass,
        private val token: MediaSessionCompat.Token,
        protected val notificationManager: Lazy<NotificationManager>

) : INotification {

    protected var builder: NotificationCompat.Builder = provideNotificationBuilder()

    private var created = false

    override fun createIfNeeded() {
        if (created){
            return
        }

        val mediaStyle = android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(token)
                .setShowActionsInCompactView(0, 1, 2)

        builder.setSmallIcon(R.drawable.vd_bird_not_singing)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(ContextCompat.getColor(service, R.color.dark_grey))
                .setColorized(false)
                .setContentIntent(buildContentIntent())
                .setDeleteIntent(buildPendingIntent(PlaybackStateCompat.ACTION_STOP))
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(mediaStyle)
                .addAction(R.drawable.vd_skip_previous, "Previous", buildPendingIntent(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
                .addAction(R.drawable.vd_pause, "PlayPause", buildPendingIntent(PlaybackStateCompat.ACTION_PLAY_PAUSE))
                .addAction(R.drawable.vd_skip_next, "Next", buildPendingIntent(PlaybackStateCompat.ACTION_SKIP_TO_NEXT))

        extendInitialization()

        created = true
    }

    protected open fun extendInitialization(){}

    override fun updateState(playbackState: PlaybackStateCompat) {
        val state = playbackState.state
        val isPlaying = state == PlaybackStateCompat.STATE_PLAYING

        val action = builder.mActions[1]
        action.actionIntent = buildPendingIntent(PlaybackStateCompat.ACTION_PLAY_PAUSE)
        action.icon = if (isPlaying) R.drawable.vd_pause else R.drawable.vd_play
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
    override fun updateMetadata(mediaEntity: MediaEntity) {
        val (_, title, artist, album) = mediaEntity

        val spannableTitle = SpannableString(title)
        spannableTitle.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, 0)

        updateMetadataImpl(spannableTitle, artist, album, Uri.parse(mediaEntity.image))
    }

    protected open fun updateMetadataImpl(title: SpannableString, artist: String, album: String, image: Uri){
        builder.setLargeIcon(ImageUtils.getBitmapFromUriWithPlaceholder(service, image))
                .setContentTitle(title)
                .setContentText(artist)
                .setSubText(title)
    }

    private fun buildContentIntent(): PendingIntent {
        return PendingIntent.getActivity(service, 0,
                Intent(service, activityClass.get()), PendingIntent.FLAG_CANCEL_CURRENT)
    }

    private fun buildPendingIntent(action: Long): PendingIntent? {
        return MediaButtonReceiver.buildMediaButtonPendingIntent(
                service, ComponentName(service, MediaButtonReceiver::class.java), action)
    }

    open fun provideNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(service)
    }

    override fun update(state: Int): Pair<android.app.Notification, Int> {
        val notification = builder.build()
        notificationManager.get().notify(INotification.NOTIFICATION_ID, notification)
        return Pair.create(notification, state)
    }
}