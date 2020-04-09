package dev.olog.service.music.notification

import android.app.Service
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.text.SpannableString
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import dev.olog.domain.schedulers.Schedulers
import dev.olog.core.dagger.ServiceLifecycle
import dev.olog.shared.TextUtils
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)
internal open class NotificationImpl24 @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    service: Service,
    mediaSession: MediaSessionCompat,
    schedulers: Schedulers

) : NotificationImpl21(lifecycle, service, mediaSession, schedulers) {

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

        builder.setContentTitle(title)
            .setContentText(artist)
    }

}