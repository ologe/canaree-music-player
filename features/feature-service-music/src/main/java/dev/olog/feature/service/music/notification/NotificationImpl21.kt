package dev.olog.feature.service.music.notification

import android.annotation.SuppressLint
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
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.shared.coroutines.autoDisposeJob
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory.SPOTIFY_TRACK
import dev.olog.domain.schedulers.Schedulers
import dev.olog.lib.image.loader.getBitmap
import dev.olog.core.dagger.ServiceLifecycle
import dev.olog.core.constants.AppConstants
import dev.olog.intents.Classes
import dev.olog.feature.service.music.R
import dev.olog.feature.service.music.interfaces.INotification
import dev.olog.feature.service.music.model.MusicNotificationState
import dev.olog.shared.android.extensions.asActivityPendingIntent
import dev.olog.shared.android.utils.assertBackgroundThread
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject

internal open class NotificationImpl21 @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    protected val service: Service,
    private val mediaSession: MediaSessionCompat,
    private val schedulers: Schedulers
) : INotification, DefaultLifecycleObserver {

    protected val notificationManager by lazy {
        service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    protected var builder = NotificationCompat.Builder(service, INotification.CHANNEL_ID)

    private var updateImageJob by autoDisposeJob()

    private var isCreated = false

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        updateImageJob = null
    }

    private fun createIfNeeded() {
        if (isCreated) {
            return
        }

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession.sessionToken)
            .setShowActionsInCompactView(1, 2, 3)

        builder.setSmallIcon(R.drawable.vd_bird_icon)
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
            .setGroup("dev.olog.msc.MUSIC") // TODO what is??

        extendInitialization()

        isCreated = true
    }

    protected open fun extendInitialization() {}

    protected open fun startChronometer(bookmark: Long) {
    }

    protected open fun stopChronometer(bookmark: Long) {
    }

    override suspend fun update(state: MusicNotificationState): Notification {
        // TODO is called too many times
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

        val notification = builder.build()
        notificationManager.notify(INotification.NOTIFICATION_ID, notification)

        updateImageJob = GlobalScope.launch(schedulers.io) {
            updateImage(state.id, state.uri, state.isPodcast)
        }

        return notification
    }

    private suspend fun updateImage(id: Long, uri: String, isPodcast: Boolean) {
        val mediaId: MediaId = if (uri.isNotBlank()) {
            MediaId.Track(SPOTIFY_TRACK, uri, "-1")
        } else {
            val category = if (isPodcast) MediaId.PODCAST_CATEGORY else MediaId.SONGS_CATEGORY
            category.playableItem(id)
        }
        val bitmap = service.getBitmap(mediaId, INotification.IMAGE_SIZE)
        yield()
        val notificationWithImage = builder.setLargeIcon(bitmap).build()
        notificationManager.notify(INotification.NOTIFICATION_ID, notificationWithImage)
    }

    @SuppressLint("RestrictedApi")
    private fun updateState(isPlaying: Boolean, bookmark: Long) {
        builder.mActions[2] = NotificationActions.playPause(service, isPlaying)
        builder.setOngoing(isPlaying)

        if (isPlaying) {
            startChronometer(bookmark)
        } else {
            stopChronometer(bookmark)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun updateFavorite(isFavorite: Boolean) {
        builder.mActions[0] = NotificationActions.favorite(service, isFavorite)
    }

    @SuppressLint("RestrictedApi")
    protected open suspend fun updateMetadataImpl(
        id: Long,
        title: SpannableString,
        artist: String,
        album: String,
        isPodcast: Boolean
    ) {
        builder.mActions[1] = NotificationActions.skipPrevious(service, isPodcast)
        builder.mActions[3] = NotificationActions.skipNext(service, isPodcast)

        builder.setContentTitle(title)
            .setContentText(artist)
            .setSubText(album)
    }

    private fun buildContentIntent(): PendingIntent {
        val intent = Intent(service, Class.forName(Classes.ACTIVITY_MAIN))
        intent.action = AppConstants.ACTION_CONTENT_VIEW
        return intent.asActivityPendingIntent(service)
    }

    override fun cancel() {
        notificationManager.cancel(INotification.NOTIFICATION_ID)
    }
}