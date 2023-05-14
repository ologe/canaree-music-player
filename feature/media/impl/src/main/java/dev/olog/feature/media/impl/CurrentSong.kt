package dev.olog.feature.media.impl

import android.app.Service
import android.util.Log
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.entity.LastMetadata
import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.entity.favorite.FavoriteStateEntity
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.gateway.track.AutoPlaylistGateway
import dev.olog.core.interactor.*
import dev.olog.core.interactor.favorite.IsFavoriteSongUseCase
import dev.olog.core.interactor.favorite.UpdateFavoriteStateUseCase
import dev.olog.core.interactor.lastplayed.InsertRecentlyPlayedUseCase
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.feature.media.impl.interfaces.IPlayerLifecycle
import dev.olog.feature.media.impl.model.MediaEntity
import dev.olog.feature.media.impl.model.MetadataEntity
import dev.olog.platform.extension.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

@ServiceScoped
class CurrentSong @Inject constructor(
    private val service: Service,
    private val schedulers: Schedulers,
    insertMostPlayedUseCase: InsertMostPlayedUseCase,
    autoPlaylistGateway: AutoPlaylistGateway,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val isFavoriteSongUseCase: IsFavoriteSongUseCase,
    private val updateFavoriteStateUseCase: UpdateFavoriteStateUseCase,
    insertLastPlayedArtistUseCase: InsertRecentlyPlayedUseCase,
    playerLifecycle: IPlayerLifecycle
) : IPlayerLifecycle.Listener {

    companion object {
        private val TAG = "SM:${CurrentSong::class.java.simpleName}"
    }

    private var isFavoriteJob: Job? = null

    private val channel = Channel<MediaEntity>(Channel.UNLIMITED)

    init {
        playerLifecycle.addListener(this)

        service.lifecycleScope.launch(schedulers.io) {
            for (entity in channel) {
                Log.v(TAG, "on new item ${entity.title}")
                insertLastPlayedArtistUseCase(entity.mediaId)

                Log.v(TAG, "insert most played ${entity.title}")
                insertMostPlayedUseCase(entity.parentMediaId, entity.mediaId)

                Log.v(TAG, "insert to history ${entity.title}")
                autoPlaylistGateway.insertToHistory(entity.mediaId)
            }
        }

    }

    override fun onPrepare(metadata: MetadataEntity) {
        updateFavorite(metadata.entity)
        saveLastMetadata(metadata.entity)
    }

    override fun onMetadataChanged(metadata: MetadataEntity) {
        channel.trySend(metadata.entity)
        updateFavorite(metadata.entity)
        saveLastMetadata(metadata.entity)
    }

    private fun updateFavorite(mediaEntity: MediaEntity) {
        Log.v(TAG, "updateFavorite ${mediaEntity.title}")

        isFavoriteJob?.cancel()
        isFavoriteJob = service.lifecycleScope.launch(schedulers.cpu) {
            val type = if (mediaEntity.isPodcast) FavoriteType.PODCAST else FavoriteType.TRACK
            val isFavorite =
                isFavoriteSongUseCase(IsFavoriteSongUseCase.Input(mediaEntity.id, type))
            val isFavoriteEnum =
                if (isFavorite) FavoriteEnum.FAVORITE else FavoriteEnum.NOT_FAVORITE
            updateFavoriteStateUseCase(FavoriteStateEntity(mediaEntity.id, isFavoriteEnum, type))
        }
    }

    private fun saveLastMetadata(entity: MediaEntity) {
        Log.v(TAG, "saveLastMetadata ${entity.title}")
        service.lifecycleScope.launch(schedulers.cpu) {
            musicPreferencesUseCase.setLastMetadata(
                LastMetadata(
                    entity.title, entity.artist, entity.id
                )
            )
        }
    }

}