package dev.olog.music_service

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import dev.olog.music_service.di.PerService
import dev.olog.music_service.interfaces.PlayerLifecycle
import dev.olog.music_service.model.MediaEntity
import dev.olog.shared.ApplicationContext
import dev.olog.shared.constants.MetadataConstants
import dev.olog.shared_android.Constants
import dev.olog.shared_android.ImageUtils
import dev.olog.shared_android.WidgetConstants
import dev.olog.shared_android.extension.getAppWidgetsIdsFor
import dev.olog.shared_android.interfaces.WidgetClasses
import java.io.File
import javax.inject.Inject

private const val IMAGE_SIZE = 300

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

        val uri = getUri(entity.image)
        val uriExist = File(uri.path).exists()

        val artist = if (entity.artist == Constants.UNKNOWN_ARTIST){
            Constants.UNKNOWN
        } else entity.artist

        val album = if (entity.album == Constants.UNKNOWN_ALBUM){
            Constants.UNKNOWN
        } else entity.album

        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, entity.mediaId.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, entity.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, entity.title)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, entity.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, entity.album)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, entity.duration)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, createBitmap(uri))
                .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, if (uriExist) uri.toString() else null)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, uri.toString())
                .putLong(MetadataConstants.IS_EXPLICIT, if(entity.isExplicit) 1L else 0L)
                .putLong(MetadataConstants.IS_REMIX, if(entity.isRemix) 1L else 0L)

        mediaSession.setMetadata(builder.build())

        notifyWidgets(entity)
    }

    private fun getUri(imageString: String): Uri {
        return if (imageString.endsWith(".webp")){
            Uri.fromFile(File(imageString))
        } else {
            Uri.parse(imageString)
        }
    }

    private fun createBitmap(uri: Uri): Bitmap? {
        return ImageUtils.getBitmapFromUriOrNull(context, uri, IMAGE_SIZE, IMAGE_SIZE)
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
                putExtra(WidgetConstants.ARGUMENT_DURATION, entity.duration)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }

            context.sendBroadcast(intent)
        }

    }

}
