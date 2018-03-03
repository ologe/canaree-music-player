package dev.olog.msc.music.service.notification

import android.app.NotificationManager
import android.app.Service
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.media.session.MediaSessionCompat
import android.text.SpannableString
import dagger.Lazy
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.getBitmap
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)
open class NotificationImpl24 @Inject constructor(
        service: Service,
        token: MediaSessionCompat.Token,
        notificationManager: Lazy<NotificationManager>

) : NotificationImpl21(service, token, notificationManager) {

    override fun startChronometer(bookmark: Long) {
        super.startChronometer(bookmark)
        builder.setSubText(null)
    }

    override fun stopChronometer(bookmark: Long) {
        super.stopChronometer(bookmark)
        builder.setSubText(TextUtils.formatTimeMillisForNotification(bookmark))
    }

    override fun updateMetadataImpl(
            id: Long,
            title: SpannableString,
            artist: String,
            album: String,
            image: String) {

        val placeholder = CoverUtils.getGradient(service, id.toInt())
        service.getBitmap(image, placeholder, INotification.IMAGE_SIZE, {
            builder.setLargeIcon(it)
                    .setContentTitle(title)
                    .setContentText(artist)
        })

    }
}