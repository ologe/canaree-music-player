package dev.olog.service.music.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.core.app.NotificationCompat
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.PendingIntentFactory
import dev.olog.image.provider.getCachedBitmap
import dev.olog.service.music.R
import dev.olog.service.music.interfaces.INotification
import dev.olog.service.music.model.MusicNotificationState
import dev.olog.intents.AppConstants
import dev.olog.intents.Classes
import dev.olog.shared.android.utils.assertBackgroundThread
import kotlinx.coroutines.yield
import javax.inject.Inject

internal open class NotificationImpl21 @Inject constructor(
    protected val service: Service,
    private val mediaSession: MediaSessionCompat,
    private val pendingIntentFactory: PendingIntentFactory,
    protected val notificationActions: NotificationActions,
) : INotification {

    protected val notificationManager by lazy {
        service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    protected var builder = NotificationCompat.Builder(service, INotification.CHANNEL_ID)

    private var isCreated = false

    private fun createIfNeeded() {
        if (isCreated) {
            return
        }

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession.sessionToken)
            .setShowActionsInCompactView(1, 2, 3)

        builder.setSmallIcon(R.drawable.vd_bird_not_singing)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(buildContentIntent())
            .setDeleteIntent(
                notificationActions.buildMediaPendingIntent(action = PlaybackStateCompat.ACTION_STOP)
            )
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(mediaStyle)
            .addAction(notificationActions.favorite(isFavorite = false))
            .addAction(notificationActions.skipPrevious(isPodcast = false))
            .addAction(notificationActions.playPause(isPlaying = false))
            .addAction(notificationActions.skipNext(isPodcast = false))
            .setGroup("dev.olog.msc.MUSIC")

        extendInitialization()

        isCreated = true
    }

    protected open fun extendInitialization() {}

    protected open fun startChronometer(bookmark: Long) {
    }

    protected open fun stopChronometer(bookmark: Long) {
    }

    override suspend fun update(state: MusicNotificationState): Notification {
        assertBackgroundThread()

        createIfNeeded()

        val title = state.title
        val artist = state.artist
        val album = state.album

        val spannableTitle = SpannableString(title)
        spannableTitle.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, 0)
        updateMetadataImpl(state.id, spannableTitle, artist, album, state.isPodcast)
        updateState(state.isPlaying, state.bookmark - state.duration)
        updateFavorite(state.isFavorite)

        yield()

        val notification = builder.build()
        notificationManager.notify(INotification.NOTIFICATION_ID, notification)
        return notification
    }

    private fun updateState(isPlaying: Boolean, bookmark: Long) {
        builder.mActions[2] = notificationActions.playPause(isPlaying = isPlaying)
        builder.setSmallIcon(if (isPlaying) R.drawable.vd_bird_singing else R.drawable.vd_bird_not_singing)
        builder.setOngoing(isPlaying)

        if (isPlaying) {
            startChronometer(bookmark)
        } else {
            stopChronometer(bookmark)
        }
    }

    private fun updateFavorite(isFavorite: Boolean) {
        builder.mActions[0] = notificationActions.favorite(isFavorite = isFavorite)
    }

    protected open suspend fun updateMetadataImpl(
        id: Long,
        title: SpannableString,
        artist: String,
        album: String,
        isPodcast: Boolean
    ) {
        builder.mActions[1] = notificationActions.skipPrevious(isPodcast = isPodcast)
        builder.mActions[3] = notificationActions.skipNext(isPodcast = isPodcast)

        val category = if (isPodcast) MediaIdCategory.PODCASTS else MediaIdCategory.SONGS
        val mediaId = MediaId.playableItem(MediaId.createCategoryValue(category, ""), id)
        val bitmap = service.getCachedBitmap(mediaId, INotification.IMAGE_SIZE)
        builder.setLargeIcon(bitmap)
            .setContentTitle(title)
            .setContentText(artist)
            .setSubText(album)
    }

    private fun buildContentIntent(): PendingIntent {
        val intent = Intent(service, Class.forName(Classes.ACTIVITY_MAIN))
        intent.action = AppConstants.ACTION_CONTENT_VIEW
        return pendingIntentFactory.activity(intent)
    }

    override fun cancel() {
        notificationManager.cancel(INotification.NOTIFICATION_ID)
    }
}