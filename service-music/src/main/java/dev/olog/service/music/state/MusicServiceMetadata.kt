package dev.olog.service.music.state

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.lib.media.MusicConstants
import dev.olog.shared.android.WidgetConstants
import dev.olog.lib.image.provider.GlideUtils
import dev.olog.lib.image.provider.getCachedBitmap
import dev.olog.lib.media.putBoolean
import dev.olog.navigation.dagger.RemoteWidgets
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.SkipType
import dev.olog.service.music.player.InternalPlayerState
import dev.olog.shared.android.extensions.getAppWidgetsIdsFor
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject

@ServiceScoped
internal class MusicServiceMetadata @Inject constructor(
    @ApplicationContext private val context: Context,
    private val schedulers: Schedulers,
    lifecycleOwner: LifecycleOwner,
    private val mediaSession: MediaSessionCompat,
    musicPrefs: MusicPreferencesGateway,
    internalPlayerState: InternalPlayerState,
    private val widgets: RemoteWidgets,
) {

    private val builder = MediaMetadataCompat.Builder()

    private var imageJob by autoDisposeJob()

    init {
        val metadataFlow = internalPlayerState.state
            .filterNotNull()
            .distinctUntilChangedBy { it.entity }

        metadataFlow.combine(musicPrefs.observeShowLockscreenArtwork())
        { entity, showArtwork -> entity to showArtwork }
            .mapLatest(::onItemChanged)
            .flowOn(schedulers.cpu)
            .launchIn(lifecycleOwner.lifecycleScope)
    }

    private fun onItemChanged(pair: Pair<InternalPlayerState.Data, Boolean>) {
        val (metadata, showLockScreenArtwork) = pair

        update(metadata, showLockScreenArtwork)
        notifyWidgets(metadata.entity)
    }

    private fun update(metadata: InternalPlayerState.Data, showLockScreenArtwork: Boolean) {
        val entity = metadata.entity

        val skipNext = metadata.skipType == SkipType.SKIP_NEXT
        val skipPrevious = metadata.skipType == SkipType.SKIP_PREVIOUS

        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, entity.mediaId.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, entity.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, entity.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, entity.album)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, entity.title)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, entity.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, entity.album)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, entity.duration.toLongMilliseconds())
            .putBoolean(MusicConstants.IS_PODCAST, entity.isPodcast)
            .putBoolean(MusicConstants.SKIP_NEXT, skipNext)
            .putBoolean(MusicConstants.SKIP_PREVIOUS, skipPrevious)
            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, null)

        mediaSession.setMetadata(builder.build())

        if (showLockScreenArtwork) {
            postImageAsync(metadata.entity)
        }
    }

    private fun postImageAsync(entity: MediaEntity) {
        imageJob = GlobalScope.launch(schedulers.io) {
            val bitmap = context.getCachedBitmap(entity.mediaId, GlideUtils.OVERRIDE_BIG)
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
            yield()
            mediaSession.setMetadata(builder.build())
        }
    }

    private fun notifyWidgets(entity: MediaEntity) {
        for (clazz in widgets.values.map { it.get() }) {
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
