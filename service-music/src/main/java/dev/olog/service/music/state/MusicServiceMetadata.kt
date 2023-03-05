package dev.olog.service.music.state

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.image.provider.GlideUtils
import dev.olog.image.provider.getCachedBitmap
import dev.olog.service.music.interfaces.IPlayerLifecycle
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.MetadataEntity
import dev.olog.service.music.model.SkipType
import dev.olog.intents.Classes
import dev.olog.intents.MusicConstants
import dev.olog.intents.WidgetConstants
import dev.olog.service.music.utils.putBoolean
import dev.olog.shared.android.extensions.getAppWidgetsIdsFor
import dev.olog.shared.android.extensions.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@ServiceScoped
internal class MusicServiceMetadata @Inject constructor(
    private val service: Service,
    private val schedulers: Schedulers,
    private val mediaSession: MediaSessionCompat,
    playerLifecycle: IPlayerLifecycle,
    private val musicPrefs: MusicPreferencesGateway

) : IPlayerLifecycle.Listener {

    companion object {
        @JvmStatic
        private val TAG = "SM:${MusicServiceMetadata::class.java.simpleName}"
    }

    private val builder = MediaMetadataCompat.Builder()

    private var showLockScreenArtwork = false

    init {
        playerLifecycle.addListener(this)

        service.lifecycleScope.launch(schedulers.cpu) {
            musicPrefs.observeShowLockscreenArtwork()
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

    private fun update(metadata: MetadataEntity) {
        Log.v(TAG, "update metadata ${metadata.entity.title}, skip type=${metadata.skipType}")

        service.lifecycleScope.launch(schedulers.cpu) {

            val entity = metadata.entity

            builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, entity.mediaId.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, entity.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, entity.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, entity.album)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, entity.title)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, entity.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, entity.album)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, entity.duration)
                .putString(MusicConstants.PATH, entity.path)
                .putBoolean(MusicConstants.IS_PODCAST, entity.isPodcast)
                .putBoolean(MusicConstants.SKIP_NEXT, metadata.skipType == SkipType.SKIP_NEXT)
                .putBoolean(MusicConstants.SKIP_PREVIOUS, metadata.skipType == SkipType.SKIP_PREVIOUS)

            yield()

            if (showLockScreenArtwork) {
                val bitmap = service.getCachedBitmap(entity.mediaId, GlideUtils.OVERRIDE_BIG)
                yield()
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
            }
            mediaSession.setMetadata(builder.build())
        }
    }

    private fun notifyWidgets(entity: MediaEntity) {
        Log.v(TAG, "notify widgets ${entity.title}")

        for (clazz in Classes.widgets) {
            val ids = service.getAppWidgetsIdsFor(clazz)

            val intent = Intent(service, clazz).apply {
                action = WidgetConstants.METADATA_CHANGED
                putExtra(WidgetConstants.ARGUMENT_SONG_ID, entity.id)
                putExtra(WidgetConstants.ARGUMENT_TITLE, entity.title)
                putExtra(WidgetConstants.ARGUMENT_SUBTITLE, entity.artist)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }

            service.sendBroadcast(intent)
        }

    }

}
