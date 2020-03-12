package dev.olog.service.music.notification

import android.app.Service
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.text.SpannableString
import androidx.annotation.RequiresApi
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.image.provider.getCachedBitmap
import dev.olog.service.music.interfaces.INotification
import dev.olog.shared.TextUtils
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)
internal open class NotificationImpl24 @Inject constructor(
    service: Service,
    mediaSession: MediaSessionCompat

) : NotificationImpl21(service, mediaSession) {

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
        builder.mActions[1] = NotificationActions.skipPrevious(service, isPodcast)
        builder.mActions[3] = NotificationActions.skipNext(service, isPodcast)

        val category = if (isPodcast) MediaIdCategory.PODCASTS else MediaIdCategory.SONGS
        val mediaId = MediaId.playableItem(MediaId.createCategoryValue(category, -1), id)
        val bitmap = service.getCachedBitmap(mediaId, INotification.IMAGE_SIZE)
        builder.setLargeIcon(bitmap)
            .setContentTitle(title)
            .setContentText(artist)
    }

}