package dev.olog.service.music.notification

import android.app.Service
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.text.SpannableString
import androidx.annotation.RequiresApi
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.PendingIntentFactory
import dev.olog.image.provider.loading.getCachedBitmap
import dev.olog.service.music.interfaces.INotification
import dev.olog.shared.TextUtils
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)
internal open class NotificationImpl24 @Inject constructor(
    service: Service,
    mediaSession: MediaSessionCompat,
    pendingIntentFactory: PendingIntentFactory,
    notificationActions: NotificationActions,
) : NotificationImpl21(
    service = service,
    mediaSession = mediaSession,
    pendingIntentFactory = pendingIntentFactory, notificationActions = notificationActions
) {

    override fun startChronometer(bookmark: Long) {
        builder.setWhen(System.currentTimeMillis() - bookmark)
            .setShowWhen(true)
            .setUsesChronometer(true)
        builder.setSubText(null)
    }

    override fun stopChronometer(bookmark: Long) {
        builder.setWhen(0)
            .setShowWhen(false)
            .setUsesChronometer(false)

        builder.setSubText(TextUtils.formatMillis(bookmark, true))
    }

    override suspend fun updateMetadataImpl(
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
    }

}