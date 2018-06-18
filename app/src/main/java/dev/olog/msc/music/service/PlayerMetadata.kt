package dev.olog.msc.music.service

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import dev.olog.msc.constants.MusicConstants
import dev.olog.msc.constants.WidgetConstants
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.scope.PerService
import dev.olog.msc.music.service.interfaces.PlayerLifecycle
import dev.olog.msc.music.service.model.MediaEntity
import dev.olog.msc.presentation.app.widget.WidgetClasses
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.k.extension.getAppWidgetsIdsFor
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

    override fun onMetadataChanged(entity: MediaEntity) {
        update(entity)
    }

    private fun update(entity: MediaEntity) {
        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, entity.mediaId.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, entity.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, DisplayableItem.adjustArtist(entity.artist))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, DisplayableItem.adjustAlbum(entity.album))
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, entity.title)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, entity.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, entity.album)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, entity.duration)
//                .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, bitmap.orElse(null)) // very slow method
                .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, entity.image)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, entity.image)
                .putString(MusicConstants.PATH, entity.path)
                .putLong(MusicConstants.IS_PODCAST, if (entity.isPodcast) 1 else 0)

        mediaSession.setMetadata(builder.build())

        notifyWidgets(entity)

    }

    private fun notifyWidgets(entity: MediaEntity){
        for (clazz in widgetClasses.get()) {
            val ids = context.getAppWidgetsIdsFor(clazz)

            val intent = Intent(context, clazz).apply {
                action = WidgetConstants.METADATA_CHANGED
                putExtra(WidgetConstants.ARGUMENT_SONG_ID, entity.id)
                putExtra(WidgetConstants.ARGUMENT_TITLE, entity.title)
                putExtra(WidgetConstants.ARGUMENT_SUBTITLE, entity.artist)
                putExtra(WidgetConstants.ARGUMENT_IMAGE, entity.image)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }

            context.sendBroadcast(intent)
        }

    }

}
