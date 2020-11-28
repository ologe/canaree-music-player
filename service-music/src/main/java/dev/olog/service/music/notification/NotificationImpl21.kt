package dev.olog.service.music.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Typeface
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.style.StyleSpan
import androidx.core.app.NotificationCompat
import androidx.core.text.buildSpannedString
import androidx.media.app.NotificationCompat.MediaStyle
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.intents.AppConstants
import dev.olog.intents.Classes
import dev.olog.lib.image.provider.getCachedBitmap
import dev.olog.service.music.R
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.player.InternalPlayerState
import dev.olog.shared.android.extensions.asActivityPendingIntent
import dev.olog.shared.android.extensions.systemService
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

internal open class NotificationImpl21 @Inject constructor(
    protected val service: Service,
    private val mediaSession: MediaSessionCompat
) : INotification {

    private val isPlayingFlow = MutableStateFlow(false)

    protected val notificationManager = service.systemService<NotificationManager>()

    protected val builder = NotificationCompat.Builder(service, INotification.CHANNEL_ID).apply {
        val mediaStyle = MediaStyle()
            .setMediaSession(mediaSession.sessionToken)
            .setShowActionsInCompactView(1, 2, 3)

        setSmallIcon(R.drawable.vd_bird_not_singing)
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setContentIntent(buildContentIntent())
        setDeleteIntent(NotificationActions.buildMediaPendingIntent(service, PlaybackStateCompat.ACTION_STOP))
        setCategory(NotificationCompat.CATEGORY_TRANSPORT)
        priority = NotificationCompat.PRIORITY_DEFAULT
        setStyle(mediaStyle)
        addAction(NotificationActions.favorite(service, false))
        addAction(NotificationActions.skipPrevious(service, false))
        addAction(NotificationActions.playPause(service, false))
        addAction(NotificationActions.skipNext(service, false))
        setGroup("dev.olog.msc.MUSIC")

        extendInitialization(this)
    }

    protected open fun extendInitialization(builder: NotificationCompat.Builder) {}
    protected open fun startChronometer(bookmark: Long) {}
    protected open fun stopChronometer(bookmark: Long) {}

    private fun post(
        notification: Notification,
        isPlaying: Boolean? = null,
    ) {
        println("post notification, isPlaying=$isPlaying")
        notificationManager.notify(INotification.NOTIFICATION_ID, notification)
        if (isPlaying ?: isPlayingFlow.value) {
            service.startForeground(INotification.NOTIFICATION_ID, notification)
        } else {
            service.stopForeground(false)
        }
    }

    override fun cancel() {
        notificationManager.cancel(INotification.NOTIFICATION_ID)
        service.stopForeground(true)
    }

    override suspend fun prepare(
        data: InternalPlayerState.Data,
        isFavorite: Boolean
    ) {
        val entity = data.entity
        updateMetadataImpl(
            id = entity.id,
            title = entity.title,
            artist = entity.artist,
            album = entity.album,
            isPodcast = entity.isPodcast
        )
        updateStateImpl(data.isPlaying, data.bookmark)
        updateFavoriteImpl(isFavorite)

        post(builder.build(), data.isPlaying)
    }

    override suspend fun updateMetadata(entity: MediaEntity) {
        updateMetadataImpl(
            id = entity.id,
            title = entity.title,
            artist = entity.artist,
            album = entity.album,
            isPodcast = entity.isPodcast
        )

        post(builder.build())
    }

    override suspend fun updateState(
        isPlaying: Boolean,
        bookmark: Long,
        duration: Long
    ) {
        updateStateImpl(isPlaying = isPlaying, bookmark = bookmark - duration)
        post(builder.build(), isPlaying)
    }

    override suspend fun updateFavorite(
        isFavorite: Boolean,
    ) {
        updateFavoriteImpl(isFavorite)
        post(builder.build())
    }

    @SuppressLint("RestrictedApi")
    private fun updateFavoriteImpl(isFavorite: Boolean) {
        builder.mActions[0] = NotificationActions.favorite(service, isFavorite)
    }

    @SuppressLint("RestrictedApi")
    private fun updateStateImpl(isPlaying: Boolean, bookmark: Long) {
        this.isPlayingFlow.value = isPlaying

        builder.mActions[2] = NotificationActions.playPause(service, isPlaying)
        builder.setSmallIcon(if (isPlaying) R.drawable.vd_bird_singing else R.drawable.vd_bird_not_singing)
        builder.setOngoing(isPlaying)

        if (isPlaying) {
            startChronometer(bookmark)
        } else {
            stopChronometer(bookmark)
        }
    }

    @SuppressLint("RestrictedApi")
    protected open suspend fun updateMetadataImpl(
        id: Long,
        title: String,
        artist: String,
        album: String,
        isPodcast: Boolean
    ) {
        val spannableTitle = buildSpannedString {
            append(title, StyleSpan(Typeface.BOLD), 0) // TODO check flag
        }

        builder.mActions[1] = NotificationActions.skipPrevious(service, isPodcast)
        builder.mActions[3] = NotificationActions.skipNext(service, isPodcast)

        val category = if (isPodcast) MediaIdCategory.PODCASTS else MediaIdCategory.SONGS
        val mediaId = MediaId.playableItem(MediaId.createCategoryValue(category, ""), id)
        val bitmap = service.getCachedBitmap(mediaId, INotification.IMAGE_SIZE)
        builder.setLargeIcon(bitmap)
            .setContentTitle(spannableTitle)
            .setContentText(artist)
            .setSubText(album)
    }

    private fun buildContentIntent(): PendingIntent {
        val intent = Intent(service, Class.forName(Classes.ACTIVITY_MAIN))
        intent.action = AppConstants.ACTION_CONTENT_VIEW
        return intent.asActivityPendingIntent(service)
    }
}