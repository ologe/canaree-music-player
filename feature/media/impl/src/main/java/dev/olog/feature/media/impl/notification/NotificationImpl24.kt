package dev.olog.feature.media.impl.notification

import android.app.Service
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.text.SpannableString
import androidx.annotation.RequiresApi
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.image.provider.getCachedBitmap
import dev.olog.feature.media.impl.interfaces.INotification
import dev.olog.core.PendingIntentFactory
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.feature.media.api.DurationUtils
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)
open class NotificationImpl24 @Inject constructor(
    service: Service,
    mediaSession: MediaSessionCompat,
    notificationActions: NotificationActions,
    pendingIntentFactory: PendingIntentFactory,
    featureMainNavigator: FeatureMainNavigator,
) : NotificationImpl21(
    service = service,
    mediaSession = mediaSession,
    notificationActions = notificationActions,
    pendingIntentFactory = pendingIntentFactory,
    featureMainNavigator = featureMainNavigator,
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

        builder.setSubText(DurationUtils.formatMillis(bookmark, true))
    }

    override suspend fun updateMetadataImpl(
        id: Long,
        title: SpannableString,
        artist: String,
        album: String,
        isPodcast: Boolean
    ) {
        builder.mActions[1] = notificationActions.skipPrevious(isPodcast)
        builder.mActions[3] = notificationActions.skipNext(isPodcast)

        val category = if (isPodcast) MediaIdCategory.PODCASTS else MediaIdCategory.SONGS
        val mediaId = MediaId.playableItem(MediaId.createCategoryValue(category, ""), id)
        val bitmap = service.getCachedBitmap(mediaId, INotification.IMAGE_SIZE)
        builder.setLargeIcon(bitmap)
            .setContentTitle(title)
            .setContentText(artist)
    }

}