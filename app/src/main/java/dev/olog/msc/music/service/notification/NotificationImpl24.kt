package dev.olog.msc.music.service.notification

import android.app.NotificationManager
import android.app.Service
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.media.session.MediaSessionCompat
import android.text.SpannableString
import dagger.Lazy
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.getBitmapAsync
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
        builder.setSubText(TextUtils.formatMillis(bookmark))
    }

    override fun updateMetadataImpl(
            id: Long,
            title: SpannableString,
            artist: String,
            album: String,
            image: String) {

        val model = DisplayableItem(0, MediaId.songId(id), "", image = image)
        val bitmap = service.getBitmapAsync(model, INotification.IMAGE_SIZE)
        builder.setLargeIcon(bitmap)
                .setContentTitle(title)
                .setContentText(artist)
    }

}