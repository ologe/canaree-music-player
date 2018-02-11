package dev.olog.msc.music.service.notification

import android.app.NotificationManager
import android.app.Service
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.SpannableString
import dagger.Lazy
import dev.olog.msc.music.service.interfaces.INotification
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.img.ImageUtils
import dev.olog.shared_android.interfaces.MainActivityClass
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)
open class NotificationImpl24 @Inject constructor(
        service: Service,
        activityClass: MainActivityClass,
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
            image: Uri) {

        builder.setLargeIcon(ImageUtils.getBitmapFromUriWithPlaceholder(service, image, id,
                                INotification.IMAGE_SIZE, INotification.IMAGE_SIZE))
                .setContentTitle(title)
                .setContentText(artist)
    }
}