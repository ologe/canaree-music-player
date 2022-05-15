package dev.olog.feature.media

import androidx.lifecycle.DefaultLifecycleObserver
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.MediaId
import dev.olog.core.ServiceScope
import dev.olog.core.entity.LastMetadata
import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.entity.favorite.FavoriteStateEntity
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.interactor.InsertHistorySongUseCase
import dev.olog.core.interactor.InsertMostPlayedUseCase
import dev.olog.core.interactor.favorite.IsFavoriteSongUseCase
import dev.olog.core.interactor.favorite.UpdateFavoriteStateUseCase
import dev.olog.core.interactor.lastplayed.InsertLastPlayedAlbumUseCase
import dev.olog.core.interactor.lastplayed.InsertLastPlayedArtistUseCase
import dev.olog.core.schedulers.Schedulers
import dev.olog.feature.media.api.MusicPreferencesGateway
import dev.olog.feature.media.api.model.MediaEntity
import dev.olog.feature.media.api.model.MetadataEntity
import dev.olog.feature.media.interfaces.IPlayerLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@ServiceScoped
internal class CurrentSong @Inject constructor(
    insertMostPlayedUseCase: InsertMostPlayedUseCase,
    insertHistorySongUseCase: InsertHistorySongUseCase,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val isFavoriteSongUseCase: IsFavoriteSongUseCase,
    private val updateFavoriteStateUseCase: UpdateFavoriteStateUseCase,
    insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
    insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase,
    playerLifecycle: IPlayerLifecycle,
    private val serviceScope: ServiceScope,
    schedulers: Schedulers,
) : DefaultLifecycleObserver,
    IPlayerLifecycle.Listener {

    private var isFavoriteJob: Job? = null

    private val channel = Channel<MediaEntity>(Channel.UNLIMITED)

    init {
        playerLifecycle.addListener(this)

        serviceScope.launch(schedulers.io) {
            for (entity in channel) {
                if (entity.mediaId.isArtist || entity.mediaId.isPodcastArtist) {
                    insertLastPlayedArtistUseCase(entity.mediaId)
                } else if (entity.mediaId.isAlbum || entity.mediaId.isPodcastAlbum) {
                    insertLastPlayedAlbumUseCase(entity.mediaId)
                }

                MediaId.playableItem(entity.mediaId, entity.id)
                insertMostPlayedUseCase(entity.mediaId)

                insertHistorySongUseCase(
                    InsertHistorySongUseCase.Input(
                        entity.id,
                        entity.isPodcast
                    )
                )
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
        isFavoriteJob?.cancel()
        isFavoriteJob = serviceScope.launch {
            val type = if (mediaEntity.isPodcast) FavoriteType.PODCAST else FavoriteType.TRACK
            val isFavorite =
                isFavoriteSongUseCase(IsFavoriteSongUseCase.Input(mediaEntity.id, type))
            val isFavoriteEnum =
                if (isFavorite) FavoriteEnum.FAVORITE else FavoriteEnum.NOT_FAVORITE
            updateFavoriteStateUseCase(FavoriteStateEntity(mediaEntity.id, isFavoriteEnum, type))
        }
    }

    private fun saveLastMetadata(entity: MediaEntity) {
        serviceScope.launch {
            musicPreferencesUseCase.setLastMetadata(
                LastMetadata(
                    entity.title, entity.artist, entity.id
                )
            )
        }
    }

}