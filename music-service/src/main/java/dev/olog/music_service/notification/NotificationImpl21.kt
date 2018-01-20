package dev.olog.music_service.notification

import android.app.Notification
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
import dagger.Lazy
import dev.olog.music_service.MusicService
import dev.olog.music_service.R
import dev.olog.music_service.di.PerService
import dev.olog.music_service.interfaces.INotification
import dev.olog.music_service.model.MediaEntity
import dev.olog.shared.constants.FloatingInfoConstants
import dev.olog.shared.constants.MusicConstants
import dev.olog.shared_android.ImageUtils
import dev.olog.shared_android.interfaces.MainActivityClass
import javax.inject.Inject

@PerService
open class NotificationImpl21 @Inject constructor(
        protected val service: Service,
        private val activityClass: MainActivityClass,
        private val token: MediaSessionCompat.Token,
        protected val notificationManager: Lazy<NotificationManager>

) : INotification {

    companion object {
        const val CHANNEL_ID = "0x6d7363"
    }

    protected var builder = NotificationCompat.Builder(service, CHANNEL_ID)

    private var isCreated = false

    override fun createIfNeeded() {
        if (isCreated){
            return
        }

        val mediaStyle = android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(token)
                .setShowActionsInCompactView(2, 3, 4)

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
                .addAction(R.drawable.vd_not_favorite, "favorite", buildUpdateFavoriteIntent())
                .addAction(R.drawable.vd_skip_previous, "Previous", buildPendingIntent(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
                .addAction(R.drawable.vd_pause_big, "PlayPause", buildPendingIntent(PlaybackStateCompat.ACTION_PLAY_PAUSE))
                .addAction(R.drawable.vd_skip_next, "Next", buildPendingIntent(PlaybackStateCompat.ACTION_SKIP_TO_NEXT))

        extendInitialization()

        isCreated = true
    }

    protected open fun extendInitialization(){}

    override fun getNotification(): Notification {
        val notification = builder
                .setContentTitle("")
                .setContentText("")
                .build()
        notificationManager.get().notify(INotification.NOTIFICATION_ID, notification)
        return notification
    }

    override fun updateState(playbackState: PlaybackStateCompat) {
        val state = playbackState.state
        val isPlaying = state == PlaybackStateCompat.STATE_PLAYING

        val action = builder.mActions[3]
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
    override fun updateMetadata(metadataWithFavorite: Pair<MediaEntity, Boolean>) {
        val (mediaEntity, isFavorite) = metadataWithFavorite
        val title = mediaEntity.title
        val artist = mediaEntity.artist
        val album = mediaEntity.album

        val spannableTitle = SpannableString(title)
        spannableTitle.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, 0)

        updateMetadataImpl(mediaEntity.id, spannableTitle, artist, album, Uri.parse(mediaEntity.image), isFavorite)
    }

    protected open fun updateMetadataImpl (
            id: Long,
            title: SpannableString,
            artist: String,
            album: String,
            image: Uri,
            isFavorite: Boolean){

        builder.setLargeIcon(ImageUtils.getBitmapFromUriWithPlaceholder(service, image, id))
                .setContentTitle(title)
                .setContentText(artist)
                .setSubText(album)

        val isFavoriteAction = builder.mActions[1]
        isFavoriteAction.icon = if (isFavorite) R.drawable.vd_favorite else R.drawable.vd_not_favorite
    }

    private fun buildFloatingInfoPendingIntent(): PendingIntent {
        val intent = Intent(service, activityClass.get())
        intent.action = FloatingInfoConstants.ACTION_START_SERVICE
        return PendingIntent.getActivity(service, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildUpdateFavoriteIntent(): PendingIntent {
        val intent = Intent(service, MusicService::class.java)
        intent.action = MusicConstants.ACTION_TOGGLE_FAVORITE
        return PendingIntent.getService(service, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildContentIntent(): PendingIntent {
        return PendingIntent.getActivity(service, 0,
                Intent(service, activityClass.get()), PendingIntent.FLAG_UPDATE_CURRENT)
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

    override fun updateFavoriteState(isFavorite: Boolean) {
        if (isCreated){
            val isFavoriteAction = builder.mActions[1]
            isFavoriteAction.icon = if (isFavorite) R.drawable.vd_favorite else R.drawable.vd_not_favorite
            notificationManager.get().notify(INotification.NOTIFICATION_ID, builder.build())
        }
    }
}