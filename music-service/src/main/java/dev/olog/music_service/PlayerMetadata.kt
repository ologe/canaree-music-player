package dev.olog.music_service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import dev.olog.music_service.di.PerService
import dev.olog.music_service.model.MediaEntity
import dev.olog.music_service.utils.ImageUtils
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.unsubscribe
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@PerService
class PlayerMetadata @Inject constructor(
        @ApplicationContext private val context: Context,
        private val mediaSession: MediaSessionCompat

) : DefaultLifecycleObserver {

    private val builder = MediaMetadataCompat.Builder()
    private var disposable: Disposable? = null

    fun update(entity: MediaEntity) {

        disposable.unsubscribe()

        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, MediaIdHelper.songId(entity.id))
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, entity.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, entity.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, entity.album)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, entity.duration)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, entity.image)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, ImageUtils.getBitmapFromUri(context, entity.image))
                .putLong("explicit", if(entity.isExplicit) 1L else 0L)
                .putLong("remix", if(entity.isRemix) 1L else 0L)
                .build()

        mediaSession.setMetadata(builder.build())
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposable.unsubscribe()
    }

}
