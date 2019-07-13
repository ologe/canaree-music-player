package dev.olog.service.music.state

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.request.target.Target
import dev.olog.core.dagger.ApplicationContext
import dev.olog.image.provider.getBitmapAsync
import dev.olog.injection.dagger.PerService
import dev.olog.media.putBoolean
import dev.olog.service.music.R
import dev.olog.service.music.interfaces.PlayerLifecycle
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.MetadataEntity
import dev.olog.service.music.model.SkipType
import dev.olog.shared.Classes
import dev.olog.shared.MusicConstants
import dev.olog.shared.WidgetConstants
import dev.olog.shared.extensions.getAppWidgetsIdsFor
import dev.olog.shared.observeKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@PerService
class MusicServiceMetadata @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaSession: MediaSessionCompat,
    playerLifecycle: PlayerLifecycle,
    private val sharedPrefs: SharedPreferences

) : PlayerLifecycle.Listener,
    DefaultLifecycleObserver,
    CoroutineScope by MainScope() {

    private val builder = MediaMetadataCompat.Builder()

    private var showLockScreenArtwork = false

    init {
        playerLifecycle.addListener(this)

        launch {
            sharedPrefs.observeKey(context.getString(R.string.prefs_lockscreen_artwork_key), false)
                .collect { showLockScreenArtwork = it }
        }
    }

    override fun onPrepare(metadata: MetadataEntity) {
        onMetadataChanged(metadata)
    }

    override fun onMetadataChanged(metadata: MetadataEntity) {
        update(metadata)
        notifyWidgets(metadata.entity)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cancel()
    }

    private fun update(metadata: MetadataEntity) {
        val entity = metadata.entity

        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, entity.mediaId.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, entity.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, entity.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, entity.album)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, entity.title)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, entity.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, entity.album)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, entity.duration)
//                .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, entity.image) TODO ??
//                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, entity.image)
            .putString(MusicConstants.PATH, entity.path)
            .putBoolean(MusicConstants.IS_PODCAST, entity.isPodcast)
            .putBoolean(MusicConstants.SKIP_NEXT, metadata.skipType == SkipType.SKIP_NEXT)
            .putBoolean(MusicConstants.SKIP_PREVIOUS, metadata.skipType == SkipType.SKIP_PREVIOUS)

        if (showLockScreenArtwork) {
            context.getBitmapAsync(entity.mediaId, Target.SIZE_ORIGINAL) { bitmap ->
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                mediaSession.setMetadata(builder.build())
            }
        } else {
            mediaSession.setMetadata(builder.build())
        }
    }

    private fun notifyWidgets(entity: MediaEntity) {
        for (clazz in Classes.widgets) {
            val ids = context.getAppWidgetsIdsFor(clazz)

            val intent = Intent(context, clazz).apply {
                action = WidgetConstants.METADATA_CHANGED
                putExtra(WidgetConstants.ARGUMENT_SONG_ID, entity.id)
                putExtra(WidgetConstants.ARGUMENT_TITLE, entity.title)
                putExtra(WidgetConstants.ARGUMENT_SUBTITLE, entity.artist)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }

            context.sendBroadcast(intent)
        }

    }

}
