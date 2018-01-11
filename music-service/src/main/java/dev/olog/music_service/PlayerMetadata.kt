package dev.olog.music_service

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import dev.olog.music_service.di.PerService
import dev.olog.music_service.interfaces.PlayerLifecycle
import dev.olog.music_service.model.MediaEntity
import dev.olog.shared.ApplicationContext
import dev.olog.shared.constants.MetadataConstants
import dev.olog.shared_android.ImageUtils
import dev.olog.shared_android.WidgetConstants
import dev.olog.shared_android.interfaces.WidgetClasses
import javax.inject.Inject

@PerService
class PlayerMetadata @Inject constructor(
        @ApplicationContext private val context: Context,
        private val mediaSession: MediaSessionCompat,
        playerLifecycle: PlayerLifecycle,
        private val widgetClasses: WidgetClasses

) : PlayerLifecycle.Listener {

    private val builder = MediaMetadataCompat.Builder()

    init {
        playerLifecycle.addListener(this)
    }

    override fun onPrepare(entity: MediaEntity) {
        update(entity)
    }

    override fun onPlay(entity: MediaEntity) {
        update(entity)
    }

    private fun update(entity: MediaEntity) {

        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, entity.mediaId.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, entity.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, entity.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, entity.album)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, entity.duration)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, entity.image)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, ImageUtils.getBitmapFromUri(context, entity.image))
                .putLong(MetadataConstants.IS_EXPLICIT, if(entity.isExplicit) 1L else 0L)
                .putLong(MetadataConstants.IS_REMIX, if(entity.isRemix) 1L else 0L)
                .build()

        mediaSession.setMetadata(builder.build())

        notifyWidgets(entity)
    }

    private fun notifyWidgets(entity: MediaEntity){
        for (clazz in widgetClasses.get()) {
            val intent = Intent(context, clazz)
            intent.action = WidgetConstants.METADATA_CHANGED
            intent.putExtra(WidgetConstants.ARGUMENT_SONG_ID, entity.id)
            intent.putExtra(WidgetConstants.ARGUMENT_TITLE, entity.title)
            intent.putExtra(WidgetConstants.ARGUMENT_SUBTITLE, entity.artist)
            intent.putExtra(WidgetConstants.ARGUMENT_IMAGE, entity.image)

            val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
                    ComponentName(context, clazz))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }

    }

}
