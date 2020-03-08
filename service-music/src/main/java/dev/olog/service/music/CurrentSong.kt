package dev.olog.service.music

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.olog.core.MediaId
import dev.olog.core.entity.LastMetadata
import dev.olog.core.entity.favorite.FavoriteEntity
import dev.olog.core.entity.favorite.FavoriteState
import dev.olog.core.entity.favorite.FavoriteTrackType
import dev.olog.core.interactor.InsertHistorySongUseCase
import dev.olog.core.interactor.InsertMostPlayedUseCase
import dev.olog.core.interactor.favorite.IsFavoriteSongUseCase
import dev.olog.core.interactor.favorite.UpdateFavoriteStateUseCase
import dev.olog.core.interactor.lastplayed.InsertLastPlayedAlbumUseCase
import dev.olog.core.interactor.lastplayed.InsertLastPlayedArtistUseCase
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.injection.dagger.PerService
import dev.olog.service.music.interfaces.IPlayerLifecycle
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.MetadataEntity
import dev.olog.shared.CustomScope
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@PerService
internal class CurrentSong @Inject constructor(
    insertMostPlayedUseCase: InsertMostPlayedUseCase,
    insertHistorySongUseCase: InsertHistorySongUseCase,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val isFavoriteSongUseCase: IsFavoriteSongUseCase,
    private val updateFavoriteStateUseCase: UpdateFavoriteStateUseCase,
    insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
    insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase,
    playerLifecycle: IPlayerLifecycle,
    private val schedulers: Schedulers

) : DefaultLifecycleObserver,
    CoroutineScope by CustomScope(schedulers.cpu),
    IPlayerLifecycle.Listener {

    companion object {
        @JvmStatic
        private val TAG = "SM:${CurrentSong::class.java.simpleName}"
    }

    private var isFavoriteJob by autoDisposeJob()

    private val channel = Channel<MediaEntity>(Channel.UNLIMITED)

    init {
        playerLifecycle.addListener(this)

        launch(schedulers.io) {
            for (entity in channel) {
                Timber.v("$TAG on new item ${entity.title}")
                if (entity.mediaId.isArtist || entity.mediaId.isPodcastArtist) {
                    Timber.v("$TAG insert last played artist ${entity.title}")
                    insertLastPlayedArtistUseCase(entity.mediaId)
                } else if (entity.mediaId.isAlbum) {
                    Timber.v("$TAG insert last played album ${entity.title}")
                    insertLastPlayedAlbumUseCase(entity.mediaId)
                }

                Timber.v("$TAG insert most played ${entity.title}")
                MediaId.playableItem(entity.mediaId, entity.id)
                insertMostPlayedUseCase(entity.mediaId)

                Timber.v("$TAG insert to history ${entity.title}")
                insertHistorySongUseCase(
                    InsertHistorySongUseCase.Input(
                        entity.id,
                        entity.isPodcast
                    )
                )
            }
        }

    }

    override fun onDestroy(owner: LifecycleOwner) {
        isFavoriteJob = null
        channel.close()
        cancel()
    }

    override fun onPrepare(metadata: MetadataEntity) {
        updateFavorite(metadata.entity)
        saveLastMetadata(metadata.entity)
    }

    override fun onMetadataChanged(metadata: MetadataEntity) {
        channel.offer(metadata.entity)
        updateFavorite(metadata.entity)
        saveLastMetadata(metadata.entity)
    }

    private fun updateFavorite(mediaEntity: MediaEntity) {
        Timber.v("$TAG updateFavorite ${mediaEntity.title}")

        isFavoriteJob = launch {
            val type = if (mediaEntity.isPodcast) FavoriteTrackType.PODCAST else FavoriteTrackType.TRACK
            val isFavorite =
                isFavoriteSongUseCase(mediaEntity.id, type)
            val isFavoriteEnum =
                if (isFavorite) FavoriteState.FAVORITE else FavoriteState.NOT_FAVORITE
            updateFavoriteStateUseCase(FavoriteEntity(mediaEntity.id, isFavoriteEnum, type))
        }
    }

    private fun saveLastMetadata(entity: MediaEntity) {
        Timber.v("$TAG saveLastMetadata ${entity.title}")
        launch {
            musicPreferencesUseCase.setLastMetadata(
                LastMetadata(
                    entity.title, entity.artist, entity.id
                )
            )
        }
    }

}