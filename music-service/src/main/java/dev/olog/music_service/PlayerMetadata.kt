package dev.olog.music_service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import dev.olog.music_service.di.PerService
import dev.olog.music_service.model.MediaEntity
import dev.olog.shared.ApplicationContext
import dev.olog.shared.constants.MetadataConstants
import dev.olog.shared_android.ImageUtils
import javax.inject.Inject

@PerService
class PlayerMetadata @Inject constructor(
        @ApplicationContext private val context: Context,
        private val mediaSession: MediaSessionCompat,
        private val currentSong: CurrentSong

) : DefaultLifecycleObserver {

    private val builder = MediaMetadataCompat.Builder()

    fun update(entity: MediaEntity, isFromPrepare: Boolean = false) {

        if (!isFromPrepare){
            currentSong.update(entity)
        }
        currentSong.setFloatingInfoCurrentItem(entity)

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
    }

}
