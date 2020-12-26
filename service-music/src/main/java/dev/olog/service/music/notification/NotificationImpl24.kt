package dev.olog.service.music.notification

import android.annotation.SuppressLint
import android.app.Service
import android.graphics.Typeface
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.text.style.StyleSpan
import androidx.annotation.RequiresApi
import androidx.core.text.buildSpannedString
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.lib.image.provider.getCachedBitmap
import dev.olog.shared.TextUtils
import javax.inject.Inject
import kotlin.time.Duration

@RequiresApi(Build.VERSION_CODES.N)
internal open class NotificationImpl24 @Inject constructor(
    service: Service,
    mediaSession: MediaSessionCompat

) : NotificationImpl21(service, mediaSession) {

    override fun startChronometer(bookmark: Duration) {
        builder.setWhen(System.currentTimeMillis() - bookmark.toLongMilliseconds())
            .setShowWhen(true)
            .setUsesChronometer(true)
        builder.setSubText(null)
    }

    override fun stopChronometer(bookmark: Duration) {
        builder.setWhen(0)
            .setShowWhen(false)
            .setUsesChronometer(false)

        builder.setSubText(TextUtils.formatMillis(bookmark.toLongMilliseconds(), true))
    }

    @SuppressLint("RestrictedApi")
    override suspend fun updateMetadataImpl(
        id: Long,
        title: String,
        artist: String,
        album: String,
        isPodcast: Boolean
    ) {
        builder.mActions[1] = NotificationActions.skipPrevious(service, isPodcast)
        builder.mActions[3] = NotificationActions.skipNext(service, isPodcast)

        val spannableTitle = buildSpannedString {
            append(title, StyleSpan(Typeface.BOLD), 0)
        }

        val category = if (isPodcast) MediaIdCategory.PODCASTS else MediaIdCategory.SONGS
        val mediaId = MediaId.playableItem(MediaId.createCategoryValue(category, "all"), id)
        val bitmap = service.getCachedBitmap(mediaId, INotification.IMAGE_SIZE)
        builder.setLargeIcon(bitmap)
            .setContentTitle(spannableTitle)
            .setContentText(artist)
    }

}