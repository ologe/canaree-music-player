package dev.olog.feature.media.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.graphics.Typeface
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.core.app.NotificationCompat
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.feature.media.R
import dev.olog.feature.media.interfaces.INotification
import dev.olog.feature.media.api.model.MusicNotificationState
import dev.olog.image.provider.getCachedBitmap
import dev.olog.shared.extension.asActivityPendingIntent
import kotlinx.coroutines.yield
import javax.inject.Inject

internal open class NotificationImpl21 @Inject constructor(
    protected val service: Service,
    private val mediaSession: MediaSessionCompat,
    private val featureMainNavigator: FeatureMainNavigator,
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
                NotificationActions.buildMediaPendingIntent(
                    service,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(mediaStyle)
            .addAction(NotificationActions.favorite(service, false))
            .addAction(NotificationActions.skipPrevious(service, false))
            .addAction(NotificationActions.playPause(service, false))
            .addAction(NotificationActions.skipNext(service, false))
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
        builder.mActions[2] = NotificationActions.playPause(service, isPlaying)
        builder.setSmallIcon(if (isPlaying) R.drawable.vd_bird_singing else R.drawable.vd_bird_not_singing)
        builder.setOngoing(isPlaying)

        if (isPlaying) {
            startChronometer(bookmark)
        } else {
            stopChronometer(bookmark)
        }
    }

    private fun updateFavorite(isFavorite: Boolean) {
        builder.mActions[0] = NotificationActions.favorite(service, isFavorite)
    }

    protected open suspend fun updateMetadataImpl(
        id: Long,
        title: SpannableString,
        artist: String,
        album: String,
        isPodcast: Boolean
    ) {
        builder.mActions[1] = NotificationActions.skipPrevious(service, isPodcast)
        builder.mActions[3] = NotificationActions.skipNext(service, isPodcast)

        val category = if (isPodcast) MediaIdCategory.PODCASTS else MediaIdCategory.SONGS
        val mediaId = MediaId.playableItem(MediaId.createCategoryValue(category, ""), id)
        val bitmap = service.getCachedBitmap(mediaId, INotification.IMAGE_SIZE)
        builder.setLargeIcon(bitmap)
            .setContentTitle(title)
            .setContentText(artist)
            .setSubText(album)
    }

    private fun buildContentIntent(): PendingIntent {
        val intent = featureMainNavigator.newIntent(service)
        intent.action = FeatureMainNavigator.ACTION_CONTENT_VIEW
        return intent.asActivityPendingIntent(service)
    }

    override fun cancel() {
        notificationManager.cancel(INotification.NOTIFICATION_ID)
    }
}