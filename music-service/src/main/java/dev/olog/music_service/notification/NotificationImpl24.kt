package dev.olog.music_service.notification

import android.app.NotificationManager
import android.app.Service
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.SpannableString
import dagger.Lazy
import dev.olog.music_service.R
import dev.olog.music_service.di.PerService
import dev.olog.music_service.interfaces.ActivityClass
import dev.olog.shared_android.ImageUtils
import dev.olog.shared_android.TextUtils
import dev.olog.shared_android.TextUtils.MIDDLE_DOT_SPACED
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)
@PerService
open class NotificationImpl24 @Inject constructor(
        service: Service,
        activityClass: ActivityClass,
        token: MediaSessionCompat.Token,
        notificationManager: Lazy<NotificationManager>

) : NotificationImpl21(service, activityClass, token, notificationManager) {

    override fun startChronometer(playbackState: PlaybackStateCompat) {
        super.startChronometer(playbackState)
        builder.setSubText(null)
    }

    override fun stopChronometer(playbackState: PlaybackStateCompat) {
        super.stopChronometer(playbackState)
        builder.setSubText(TextUtils.formatTimeMillisForNotification(playbackState.position))
    }

    override fun updateMetadataImpl(
            id: Long,
            title: SpannableString,
            artist: String,
            album: String,
            image: Uri,
            isFavorite: Boolean) {

        builder.setLargeIcon(ImageUtils.getBitmapFromUriWithPlaceholder(service, image, id))
                .setContentTitle(title)
                .setContentText(artist + MIDDLE_DOT_SPACED + album)

        val isFavoriteAction = builder.mActions[1]
        isFavoriteAction.icon = if (isFavorite) R.drawable.vd_favorite else R.drawable.vd_not_favorite
    }
}