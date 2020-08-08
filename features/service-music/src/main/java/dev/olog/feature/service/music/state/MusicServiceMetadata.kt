package dev.olog.feature.service.music.state

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.shared.coroutines.autoDisposeJob
import dev.olog.domain.prefs.MusicPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.lib.image.loader.GlideUtils
import dev.olog.lib.image.loader.getCachedBitmap
import dev.olog.core.dagger.ServiceLifecycle
import dev.olog.intents.Classes
import dev.olog.intents.MusicConstants
import dev.olog.intents.WidgetConstants
import dev.olog.feature.service.music.interfaces.IPlayerLifecycle
import dev.olog.feature.service.music.model.MediaEntity
import dev.olog.feature.service.music.model.MetadataEntity
import dev.olog.feature.service.music.model.SkipType
import dev.olog.shared.android.extensions.getAppWidgetsIdsFor
import dev.olog.shared.android.extensions.putBoolean
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@ServiceScoped
internal class MusicServiceMetadata @Inject constructor(
    @ServiceLifecycle private val lifecycle: Lifecycle,
    @ApplicationContext private val context: Context,
    private val mediaSession: MediaSessionCompat,
    playerLifecycle: IPlayerLifecycle,
    musicPrefs: MusicPreferencesGateway,
    private val schedulers: Schedulers
) : IPlayerLifecycle.Listener {

    companion object {
        @JvmStatic
        private val TAG = "SM:${MusicServiceMetadata::class.java.simpleName}"
    }

    private val builder = MediaMetadataCompat.Builder()

    private var showLockScreenArtwork = false

    private var imageJob by autoDisposeJob()

    init {
        playerLifecycle.addListener(this)

        musicPrefs.observeShowLockscreenArtwork()
            .onEach { showLockScreenArtwork = it }
            .launchIn(lifecycle.coroutineScope)
    }

    override fun onPrepare(metadata: MetadataEntity) {
        onMetadataChanged(metadata)
    }

    override fun onMetadataChanged(metadata: MetadataEntity) {
        update(metadata)
        notifyWidgets(metadata.entity)
    }

    private fun update(metadata: MetadataEntity) {
        Timber.v("$TAG update metadata ${metadata.entity.title}, skip type=${metadata.skipType}")

        imageJob = lifecycle.coroutineScope.launch(schedulers.cpu) {

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

            if (showLockScreenArtwork) {
                val bitmap = withContext(schedulers.io) {
                    context.getCachedBitmap(entity.mediaId, GlideUtils.OVERRIDE_BIG)
                }
                yield()
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
            }
            mediaSession.setMetadata(builder.build())
        }
    }

    private fun notifyWidgets(entity: MediaEntity) {
        Timber.v("$TAG notify widgets ${entity.title}")

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
